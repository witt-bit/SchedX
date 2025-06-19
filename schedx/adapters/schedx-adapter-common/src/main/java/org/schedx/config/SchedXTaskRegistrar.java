package org.schedx.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.schedx.SchedXConfigurationException;
import org.schedx.SchedXException;
import org.schedx.SchedXTaskException;
import org.schedx.listen.SchedX2ApplicationEventBridgeListener;
import org.schedx.listen.SchedXListener;
import org.schedx.listen.SchedXListenerProxy;
import org.schedx.listen.event.SchedXGroupEvent;
import org.schedx.listen.event.SchedXJobEvent;
import org.schedx.listen.event.SchedXJobState;
import org.schedx.listen.event.SchedXState;
import org.schedx.listen.event.SchedXTaskEvent;
import org.schedx.listen.event.SchedXTaskState;
import org.schedx.lock.NoOpTaskLocker;
import org.schedx.lock.SchedXTaskLocker;
import org.schedx.persistence.SchedXGroup;
import org.schedx.persistence.SchedXPersistence;
import org.schedx.persistence.SchedXPersistenceException;
import org.schedx.persistence.SchedXTask;
import org.schedx.persistence.SchedXTaskTrigger;
import org.schedx.util.ReflectionUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.FixedRateTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.Task;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <p>{@link ScheduledTaskRegistrar}基本增强实现</p>
 * <p>创建于 2025-05-03 22:46 22:46 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @see ScheduledTaskRegistrar
 * @since 0.0.1
 */
@Slf4j
public class SchedXTaskRegistrar extends ScheduledTaskRegistrar {

    @Getter
    private SchedXTaskLocker taskLocker = NoOpTaskLocker.INSTANCE;

    @Getter
    private final SchedXListenerProxy listener = new SchedXListenerProxy();

    @Getter
    private volatile boolean applicationReady = false;

    /**
     * Schedx实例
     */
    protected final SchedXGroup schedX = new SchedXGroup();

    /**
     * 任务寄存器
     */
    protected final List<TaskRegister> taskRegisters = new ArrayList<>(8);

    /**
     * 冗余{@code org.springframework.scheduling.config.ScheduledTaskRegistrar#unresolvedTasks}, 持有其引用，因其定义是{@code private}
     */
    private final Map<Task, ScheduledTask> unresolvedTasks;

    /**
     * 冗余{@code org.springframework.scheduling.config.ScheduledTaskRegistrar#scheduledTasks}, 持有其引用，因其定义是{@code private}
     */
    private final Set<ScheduledTask> scheduledTasks;

    /**
     * 持有{@code org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#scheduledTasks},用于在销毁时，提前销毁该bean的所有Task,避免调用异常
     */
    @Setter
    @NonNull
    protected Map<Object, Set<ScheduledTask>> beanTasks;

    @Setter(AccessLevel.MODULE)
    private String springApplicationName;

    @Setter(AccessLevel.MODULE)
    protected ApplicationEventPublisher applicationEventPublisher;

    @Getter
    private final SchedXProperties schedXProperties;

    @Setter
    protected SchedXPersistence persistence;

    public SchedXTaskRegistrar(SchedXProperties schedXProperties) {
        this.schedXProperties = schedXProperties;
        this.unresolvedTasks = ReflectionUtil.getSameNameSpringField(this, "unresolvedTasks");
        this.scheduledTasks = ReflectionUtil.getSameNameSpringField(this, "scheduledTasks");
        this.addDefaultTaskRegistries();
    }

    public void initializeGroup() {
        if (null == this.persistence) {
            log.trace("No '{}' bean found !", SchedXPersistence.class.getName());
            return;
        }
        // async
        SchedXGroup schedXGroup = new SchedXGroup();
        schedXGroup.setName(this.springApplicationName);
        schedXGroup.setState(SchedXState.STARTING);
        // schedXGroup.setIp(this.schedXProperties.getIp());
        // schedXGroup.setPort(this.schedXProperties.getPort());
        String groupId = this.persistence.initializeGroup(schedXGroup);
        schedXGroup.setId(groupId);

        final SchedXGroupEvent event = new SchedXGroupEvent(groupId, schedXGroup.getIp(), schedXGroup.getState());
        this.listener.onGroupEvent(event);
    }

    /**
     * 在指定的位置插入任务注册器
     *
     * @param position     位置
     * @param taskRegister 任务注册对象
     * @see Task
     * @see TaskRegister
     * @see List#add(int, Object)
     */
    protected void addTaskRegistry(int position, TaskRegister taskRegister) {
        if (position < 0 || position >= this.taskRegisters.size()) {
            this.taskRegisters.add(taskRegister);
            return;
        }
        this.taskRegisters.add(position, taskRegister);
    }

    /**
     * 添加任务注册器，支持任务拓展
     *
     * @param taskRegister 任务注册
     */
    protected void addTaskRegistry(TaskRegister taskRegister) {
        this.addTaskRegistry(Integer.MAX_VALUE, taskRegister);
    }

    @Override
    protected void scheduleTasks() {
        if (!this.applicationReady) {
            log.trace("SchedXTaskRegistrar is not ready.");
            return;
        }
        super.scheduleTasks();
    }

    /**
     * 从{@link Scheduled}解析{@link ScheduledTask}
     *
     * @param context 语境
     * @return {@link Set }<{@link ScheduledTask }>
     */
    public Set<ScheduledTask> parseScheduledTasks(SchedXDynamicTaskContext context) {
        //
        Set<ScheduledTask> tasks = new LinkedHashSet<>(4);
        this.addParsedTask(tasks, () -> this.parseCronTask(context));
        this.addParsedTask(tasks, () -> this.parseFixedDelayTask(context));
        this.addParsedTask(tasks, () -> this.parseFixedRateTask(context));
        return tasks;
    }

    /**
     * 添加解析后的调度任务
     *
     * @param tasks       任务
     * @param taskFactory 任务工厂
     */
    protected void addParsedTask(Set<ScheduledTask> tasks, Supplier<ScheduledTask> taskFactory) {
        ScheduledTask task = taskFactory.get();
        if (null == task) {
            return;
        }

        if (!tasks.isEmpty()) {
            final String errorMessage =
                    "Exactly one of the 'cron', 'fixedDelay(String)', or 'fixedRate(String)' attributes is required";
            throw new IllegalArgumentException(errorMessage);
        }

        tasks.add(task);
    }

    /**
     * 解析{@link Scheduled#cron()}为{@link ScheduledTask}对象
     *
     * @param context 注解上下文
     * @return {@link ScheduledTask }
     */
    protected ScheduledTask parseCronTask(SchedXDynamicTaskContext context) {
        final ValueSpec<String> cronSpec = context.getCron();
        if (cronSpec.isNullValue()) {
            return null;
        }
        final ValueSpec<String> zoneSpec = context.getZone();

        final String cron = cronSpec.value();
        if (Scheduled.CRON_DISABLED.equals(cron)) {
            return null;
        }

        // tasks.add();
        return this.newCronTask(cron, zoneSpec.value(), context.getRunnable());
    }

    /**
     * 创建{@link CronTask}的{@link ScheduledTask}
     *
     * @param cron     调度表达式
     * @param zone     区域
     * @param runnable 运行
     * @return {@link ScheduledTask }
     */
    protected ScheduledTask newCronTask(String cron, String zone, Runnable runnable) {
        CronTrigger trigger;
        if (StringUtils.hasText(zone)) {
            trigger = new CronTrigger(cron, StringUtils.parseTimeZoneString(zone));
        } else {
            trigger = new CronTrigger(cron);
        }
        final CronTask cronTask = new CronTask(runnable, trigger);
        return this.scheduleCronTask(cronTask);
    }

    /**
     * 解析{@link Scheduled#fixedDelay()}和{@link Scheduled#fixedDelayString()}为{@link ScheduledTask}对象
     *
     * @param context 注解上下文
     * @return {@link ScheduledTask }
     */
    protected ScheduledTask parseFixedDelayTask(SchedXDynamicTaskContext context) {
        // Check fixed delay
        final ValueSpec<Duration> fixedDelaySpec = context.getFixedDelay();
        if (fixedDelaySpec.isNullValue()) {
            return null;
        }
        // At this point we don't need to differentiate between initial delay set or not anymore
        final Duration delayToUse = context.getInitialDelay().orElseIfNull(Duration.ZERO);

        return this.newFixedDelayTask(context.getRunnable(), fixedDelaySpec.value(), delayToUse);
    }

    /**
     * 创建{@link FixedDelayTask}的{@link ScheduledTask}
     *
     * @param runnable   可运行
     * @param fixedDelay 固定延迟
     * @param delayToUse 使用延迟
     * @return {@link ScheduledTask }
     */
    protected ScheduledTask newFixedDelayTask(Runnable runnable, Duration fixedDelay, Duration delayToUse) {
        return this.scheduleFixedDelayTask(new FixedDelayTask(runnable, fixedDelay.toMillis(), delayToUse.toMillis()));
    }

    /**
     * 解析{@link Scheduled#fixedRate()}和{@link Scheduled#fixedRateString()}
     *
     * @param context 上下文
     * @return {@link ScheduledTask }
     */
    protected ScheduledTask parseFixedRateTask(SchedXDynamicTaskContext context) {
        final ValueSpec<Duration> fixedRate = context.getFixedRate();
        if (fixedRate.isNullValue()) {
            return null;
        }

        final Duration initialDelay = context.getInitialDelay().orElseIfNull(Duration.ZERO);
        return this.newFixedRateTask(context.getRunnable(), fixedRate.value(), initialDelay);
    }

    /**
     * 创建{@link FixedRateTask}的{@link ScheduledTask}
     *
     * @param runnable     运行对象
     * @param fixedRate    固定速率
     * @param initialDelay 初始延迟
     */
    protected ScheduledTask newFixedRateTask(Runnable runnable, Duration fixedRate, Duration initialDelay) {
        return this.scheduleFixedRateTask(new FixedRateTask(runnable, fixedRate.toMillis(), initialDelay.toMillis()));
    }

    /**
     * 添加默认{@link Task}注册方式
     *
     * @see #addTaskRegistry(int, TaskRegister)
     * @see #addTaskRegistry(TaskRegister)
     */
    protected void addDefaultTaskRegistries() {
        this.addTaskRegistry(new CronTaskRegister());
        this.addTaskRegistry(new TriggerTaskRegister());
        this.addTaskRegistry(new FixedRateTaskRegister());
        this.addTaskRegistry(new FixedDelayTaskRegister());
        // this.addTaskRegistry(new OneTimeTaskRegister());
    }


    /**
     * 等价于{@code org.springframework.scheduling.config.ScheduledTaskRegistrar#addScheduledTask(ScheduledTask)}
     *
     * @param task 任务
     */
    protected void addScheduledTask(@Nullable ScheduledTask task) {
        if (task != null) {
            this.scheduledTasks.add(task);
            final Object targetBean = this.getTaskBean(task);
            this.beanTasks.computeIfAbsent(targetBean, k -> new LinkedHashSet<>()).add(task);
        }
    }

    /**
     * 执行任务
     *
     * @param task                任务
     * @param executionParameters 执行参数
     * @throws SchedXTaskException 如果执行失败
     */
    public void executeTask(Task task, String executionParameters) throws SchedXTaskException {
        final SchedXTaskRunnable schedXTaskRunnable = this.getTaskRunnable(task);
        final FutureTask<Void> futureTask = new FutureTask<>(schedXTaskRunnable, null);
        this.getExecutor().execute(futureTask);
        try {
            futureTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw SchedXTaskException.executeError(schedXTaskRunnable.getJobId(), schedXTaskRunnable.getId(), e);
        }

        // TODO 参数功能
    }

    /**
     * 取消计划任务, 从{@link #scheduledTasks}删除，迁移到{@link #unresolvedTasks}
     *
     * @param task   任务
     * @param forced 是否强制中断
     * @see java.util.concurrent.ScheduledFuture#cancel(boolean)
     */
    protected void cancelScheduledTask(ScheduledTask task, boolean forced) {
        final boolean exist = this.scheduledTasks.remove(task);
        if (!exist) {
            final String taskId = Optional.ofNullable(this.getTaskRunnable(task)).map(SchedXTaskRunnable::getId).orElse(null);
            throw SchedXTaskException.missingTaskOrStateRefreshed(null, taskId);
        }
        task.cancel(forced);
        this.unresolvedTasks.put(task.getTask(), task);
    }

    /**
     * 从{@link #unresolvedTasks}恢复任务到{@link #scheduledTasks}, 变体自{@link ScheduledTaskRegistrar#scheduleTasks()}
     *
     * @param scheduledTask 计划任务
     */
    protected void resumeTask(ScheduledTask scheduledTask) {
        final Task task = scheduledTask.getTask();
        final boolean exist = this.unresolvedTasks.remove(task, scheduledTask);
        if (!exist) {
            final String taskId = Optional.ofNullable(this.getTaskRunnable(scheduledTask)).map(SchedXTaskRunnable::getId).orElse(null);
            throw SchedXTaskException.missingTaskOrStateRefreshed(null, taskId);
        }
        this.registerTask(task);
    }

    /**
     * 注册计划任务
     *
     * @param task 任务
     */
    public final void registerTask(Task task) {
        ScheduledTask scheduledTask = null;
        for (TaskRegister taskRegister : this.taskRegisters) {
            scheduledTask = taskRegister.registry(task);
            if (scheduledTask != null) {
                break;
            }
        }

        if (null == scheduledTask) {
            throw SchedXConfigurationException.missingConfiguration("TaskRegister", task.getClass().getName());
        }
        this.addScheduledTask(scheduledTask);
    }

    /**
     * 从 {@link Task} 派生子任务
     *
     * @param task      任务
     * @param newTaskId 新任务ID
     */
    public void forkTask(Task task, String newTaskId) {
        if (null == this.persistence) {
            throw SchedXConfigurationException.missingBean(SchedXPersistence.class);
        }

        final SchedXTask schedXTask = this.persistence.loadTask(newTaskId);
        if (null == task) {
            throw SchedXPersistenceException.missingRow("Task", newTaskId);
        }

        final int scheduleType = schedXTask.getScheduleType();

        final TaskRegister taskRegister = this.taskRegisters.stream()
                .filter(tr -> tr.supportScheduleType() == schedXTask.getScheduleType())
                .findFirst()
                .orElseThrow(() -> SchedXConfigurationException.missingConfiguration("TaskRegister", "scheduleType:" + scheduleType));

        // 复制的Runnable
        final SchedXTaskRunnable oldWrappedRunnable = this.getTaskRunnable(task);

        final SchedXTaskRunnable newWrappedRunnable = oldWrappedRunnable.fork(newTaskId);

        // 创建新任务
        final SchedXDynamicTaskContext context = SchedXDynamicTaskContext.fromSchedXTask(schedXTask, newWrappedRunnable);
        final ScheduledTask newScheduledTask = taskRegister.registry(context);
        this.addScheduledTask(newScheduledTask);
    }

    /**
     * 重新加载任务
     *
     * @param taskId 任务ID
     */
    public void reload(String taskId) {
        if (null == this.persistence) {
            throw SchedXConfigurationException.missingBean(SchedXPersistence.class);
        }

        final ScheduledTask oldScheduledTask = this.getTasks().get(taskId);
        if (null == oldScheduledTask) {
            throw SchedXTaskException.missingTask(null, taskId);
        }

        final SchedXTask task = this.persistence.loadTask(taskId);
        if (null == task) {
            throw SchedXPersistenceException.missingRow("Task", taskId);
        }

        final int scheduleType = task.getScheduleType();

        final TaskRegister taskRegister = this.taskRegisters.stream()
                .filter(tr -> tr.supportScheduleType() == task.getScheduleType())
                .findFirst()
                .orElseThrow(() -> SchedXConfigurationException.missingConfiguration("TaskRegister", "scheduleType:" + scheduleType));

        // 停止旧任务
        oldScheduledTask.cancel(false);
        this.scheduledTasks.remove(oldScheduledTask);
        final Task workerTask = oldScheduledTask.getTask();
        this.unresolvedTasks.remove(workerTask);

        // 任务
        final SchedXTaskRunnable runnable = this.getTaskRunnable(workerTask);
        // 创建新任务
        final SchedXDynamicTaskContext context = SchedXDynamicTaskContext.fromSchedXTask(task, runnable);
        final ScheduledTask newScheduledTask = taskRegister.registry(context);
        this.addScheduledTask(newScheduledTask);
    }

    /**
     * 获取内置的{@link TaskScheduler}实例，在Spring流程中，{@link TaskScheduler}实际上应该是{@link ExecutorService},
     * 获取方式见@code ScheduledAnnotationBeanPostProcessor.resolveSchedulerBean(BeanFactory, Class, boolean) }，
     * 自动配置在{@code org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration.taskScheduler(TaskSchedulerBuilder)}
     *
     * @return {@link Executor }
     * @see ThreadPoolTaskScheduler
     */
    public Executor getExecutor() {
        final TaskScheduler scheduler = this.getScheduler();
        if (null == scheduler) {
            throw SchedXConfigurationException.missingBean(TaskScheduler.class);
        }

        // use ConcurrentTaskScheduler wrapped
        if (scheduler instanceof ConcurrentTaskExecutor) {
            final ConcurrentTaskExecutor executorAdapter = (ConcurrentTaskExecutor) scheduler;
            return executorAdapter.getConcurrentExecutor();
        }

        if (scheduler instanceof Executor) {
            return (Executor) scheduler;
        }

        throw SchedXConfigurationException.errorBeanConfiguration(ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME,
                scheduler.getClass().getName());
    }

    /**
     * 获取当前调度中的所有job和Task映射
     *
     * @return {@code Map<String, List<ScheduledTask>> }
     * @see #getUnresolvedJobs()
     * @see #getJobs()
     */
    public Map<String, List<ScheduledTask>> getSchedXJobs() {
        final Map<String, List<ScheduledTask>> schedXJobs = new HashMap<>();
        for (ScheduledTask scheduledTask : this.scheduledTasks) {
            final SchedXTaskRunnable schedXTaskRunnable = getTaskRunnable(scheduledTask);
            if (null == schedXTaskRunnable) {
                continue;
            }
            final String jobId = schedXTaskRunnable.getJobId();
            schedXJobs.computeIfAbsent(jobId, k -> new ArrayList<>()).add(scheduledTask);
        }

        return Collections.unmodifiableMap(schedXJobs);
    }

    /**
     * 获取当前未被调度的所有Job与Task映射
     *
     * @return {@code Map<String, ScheduledTask>}
     * @see #getSchedXJobs()
     * @see #getJobs()
     */
    public Map<String, List<ScheduledTask>> getUnresolvedJobs() {
        final Map<String, List<ScheduledTask>> unresolvedJobs = new HashMap<>();
        for (ScheduledTask scheduledTask : this.unresolvedTasks.values()) {
            final SchedXTaskRunnable schedXTaskRunnable = getTaskRunnable(scheduledTask);
            if (null == schedXTaskRunnable) {
                continue;
            }
            final String jobId = schedXTaskRunnable.getJobId();
            unresolvedJobs.computeIfAbsent(jobId, k -> new ArrayList<>()).add(scheduledTask);
        }

        return Collections.unmodifiableMap(unresolvedJobs);
    }


    /**
     * 获取系统中所有的Job与Task映射
     *
     * @return {@code Map<String, List<ScheduledTask>>}
     * @see #getSchedXJobs()
     * @see #getUnresolvedJobs()
     */
    public Map<String, List<ScheduledTask>> getJobs() {
        final Map<String, List<ScheduledTask>> schedXJobs = new HashMap<>(this.getSchedXJobs());
        // compose with jobId
        this.getUnresolvedJobs().forEach((jobId, tasks) ->
                schedXJobs.computeIfAbsent(jobId, thisKey -> new ArrayList<>()).addAll(tasks));

        return Collections.unmodifiableMap(schedXJobs);
    }

    /**
     * 获取当前调度中的所有任务
     *
     * @return {@code Map<String, ScheduledTask>}
     */
    public Map<String, ScheduledTask> getSchedXTasks() {
        final Map<String, ScheduledTask> schedXTasks = new HashMap<>();
        for (ScheduledTask scheduledTask : this.scheduledTasks) {
            final SchedXTaskRunnable schedXTaskRunnable = getTaskRunnable(scheduledTask);
            if (null == schedXTaskRunnable) {
                continue;
            }
            final String taskId = schedXTaskRunnable.getId();
            schedXTasks.put(taskId, scheduledTask);
        }

        return Collections.unmodifiableMap(schedXTasks);
    }

    /**
     * 获取当前未被调度的所有任务
     *
     * @return {@code Map<String, ScheduledTask>}
     */
    public Map<String, ScheduledTask> getUnresolvedTasks() {
        final Map<String, ScheduledTask> unresolvedTasks = new HashMap<>();
        for (ScheduledTask scheduledTask : this.unresolvedTasks.values()) {
            final SchedXTaskRunnable schedXTaskRunnable = getTaskRunnable(scheduledTask);
            if (null == schedXTaskRunnable) {
                continue;
            }
            final String taskId = schedXTaskRunnable.getId();
            unresolvedTasks.put(taskId, scheduledTask);
        }

        return Collections.unmodifiableMap(unresolvedTasks);
    }

    /**
     * 获取所有任务
     *
     * @return {@code Map<String, ScheduledTask>}
     * @see #getSchedXTasks()
     * @see #getUnresolvedTasks()
     */
    public Map<String, ScheduledTask> getTasks() {
        final Map<String, ScheduledTask> schedXTasks = new HashMap<>();
        schedXTasks.putAll(this.getSchedXTasks());
        schedXTasks.putAll(this.getUnresolvedTasks());
        return Collections.unmodifiableMap(schedXTasks);
    }

    // @Override
    // @SuppressWarnings("NullableProblems")
    // public ScheduledTask scheduleCronTask(CronTask task) {
    //     return super.scheduleCronTask(task);
    // }
    //
    // @Override
    // @SuppressWarnings("NullableProblems")
    // public ScheduledTask scheduleTriggerTask(TriggerTask task) {
    //     return super.scheduleTriggerTask(task);
    // }
    //
    // @Override
    // @SuppressWarnings("NullableProblems")
    // public ScheduledTask scheduleFixedDelayTask(FixedDelayTask task) {
    //     return super.scheduleFixedDelayTask(task);
    // }
    //
    // @Override
    // @SuppressWarnings("NullableProblems")
    // public ScheduledTask scheduleFixedRateTask(FixedRateTask task) {
    //     return super.scheduleFixedRateTask(task);
    // }

    protected void applicationReady() {
        this.applicationReady = true;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.schedXProperties.isBridgeApplicationEvent()) {
            this.addListener(new SchedX2ApplicationEventBridgeListener(this.applicationEventPublisher));
        }
        super.afterPropertiesSet();
    }

    protected void nextLifeCycle(SchedXState state) {
        this.schedX.setState(state);
        final SchedXGroupEvent event = SchedXGroupEvent.fromGroup(this.schedX);
        this.listener.onGroupEvent(event);
    }

    @Override
    public void destroy() {
        log.info("SchedX shutdown ...");

        // group
        this.nextLifeCycle(SchedXState.STOPPING);

        // job
        final Map<String, List<ScheduledTask>> allJobs = this.getSchedXJobs();
        final Set<String> allJobIds = allJobs.keySet();
        final SchedXJobEvent jobEvent = SchedXJobEvent.fromJobs(allJobIds, SchedXJobState.STOPPING);
        this.listener.onJobEvent(jobEvent);

        // task
        for (Map.Entry<String, List<ScheduledTask>> jobTaskEntry : allJobs.entrySet()) {
            final String jobId = jobTaskEntry.getKey();
            final List<ScheduledTask> tasks = jobTaskEntry.getValue();
            final Set<String> taskIds = tasks.stream()
                    .map(this::getTaskRunnable)
                    .filter(Objects::nonNull)
                    .map(SchedXTaskRunnable::getId)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            final SchedXTaskEvent taskEvent = SchedXTaskEvent.fromJob(jobId, taskIds);
            taskEvent.setState(SchedXTaskState.STOPPING);
            // 任务开始停止事件
            this.listener.onTaskEvent(taskEvent);

            // 仅SchedX相关的任务停止
            for (ScheduledTask task : tasks) {
                this.scheduledTasks.remove(task);
                task.cancel(false);
            }

            // 任务开始停止事件
            this.listener.onTaskEvent(taskEvent.withState(SchedXTaskState.STOPPED));
        }

        // job

        this.listener.onJobEvent(jobEvent.withState(SchedXJobState.OFFLINE));

        // 原来Spring逻辑
        super.destroy();

        // group
        this.nextLifeCycle(SchedXState.OFFLINE);
    }

    /**
     * 从{@link Task}中获得schedx包装的{@link Runnable}, 如果不是schedx扫描的任务，则返回{@code null}
     *
     * @param task 任务
     * @return {@link SchedXTaskRunnable }
     */
    protected SchedXTaskRunnable getTaskRunnable(Task task) {
        final Runnable runnable = task.getRunnable();
        if (!(runnable instanceof SchedXTaskRunnable)) {
            return null;
        }
        return (SchedXTaskRunnable) runnable;
    }

    /**
     * 从{@link ScheduledTask}中获得schedx包装的{@link Runnable}, 如果不是schedx扫描的任务，则返回{@code null}
     *
     * @param task 任务
     * @return {@link SchedXTaskRunnable }
     */
    protected SchedXTaskRunnable getTaskRunnable(ScheduledTask task) {
        return this.getTaskRunnable(task.getTask());
    }

    /**
     * 获取任务所属bean
     *
     * @param task 任务
     * @return {@link Object }
     */
    protected Object getTaskBean(ScheduledTask task) {
        final Runnable runnable = task.getTask().getRunnable();
        if (runnable instanceof SchedXTaskRunnable) {
            return ((SchedXTaskRunnable) runnable).getTarget();
        }

        if (runnable instanceof ScheduledMethodRunnable) {
            return ((ScheduledMethodRunnable) runnable).getTarget();
        }

        throw SchedXConfigurationException.unsupportedType(runnable.getClass());
    }

    /**
     * 设置任务锁
     *
     * @param taskLocker 任务锁实现
     * @return {@link SchedXTaskRegistrar }
     */
    public SchedXTaskRegistrar setTaskLocker(SchedXTaskLocker taskLocker) {
        if (null != taskLocker) {
            this.taskLocker = taskLocker;
        }
        return this;
    }

    /**
     * 添加Schedx监听器实现
     *
     * @param listener 监听器
     * @return {@link SchedXTaskRegistrar }
     */
    public SchedXTaskRegistrar addListener(SchedXListener listener) {
        this.listener.addListener(listener);
        return this;
    }

    /**
     * 任务注册器，将任务注册到{@link #getScheduler()}
     *
     * @author witt
     * @see #getExecutor()
     * @see #addTaskRegistry(TaskRegister)
     * @see #addTaskRegistry(int, TaskRegister)
     * @see #addCronTask(CronTask)
     */
    public interface TaskRegister {
        /**
         * 注册Task
         *
         * @param task 任务
         * @return {@link ScheduledTask }
         */
        ScheduledTask registry(Task task);

        /**
         * 返回当前支持的调度类型
         *
         * @return int
         * @see SchedXTaskTrigger#getScheduleType()
         */
        int supportScheduleType();

        // Task newTask();

        /**
         * 从{@link SchedXDynamicTaskContext}注册到调度池
         *
         * @param context 语境
         * @return {@link ScheduledTask }
         */
        ScheduledTask registry(SchedXDynamicTaskContext context);
    }

    /**
     * cron表达式类任务注册器
     *
     * @author witt
     * @see CronTask
     */
    protected class CronTaskRegister implements TaskRegister {
        @Override
        public ScheduledTask registry(Task task) {
            if (task instanceof CronTask) {
                return SchedXTaskRegistrar.this.scheduleCronTask((CronTask) task);
            }
            return null;
        }

        @Override
        public int supportScheduleType() {
            return 0;
        }

        @Override
        public ScheduledTask registry(SchedXDynamicTaskContext context) {
            return SchedXTaskRegistrar.this.parseCronTask(context);
        }


    }

    /**
     * 触发任务寄存器
     *
     * @author witt
     */
    protected class TriggerTaskRegister implements TaskRegister {
        @Override
        public ScheduledTask registry(Task task) {
            if (task instanceof TriggerTask) {
                return SchedXTaskRegistrar.this.scheduleTriggerTask((TriggerTask) task);
            }
            return null;
        }

        @Override
        public int supportScheduleType() {
            return -1;
        }

        @Override
        public ScheduledTask registry(SchedXDynamicTaskContext context) {
            throw SchedXException.never();
        }
    }

    /**
     * 固定速率任务注册器
     *
     * @author witt
     * @see FixedRateTask
     */
    protected class FixedRateTaskRegister implements TaskRegister {
        @Override
        public ScheduledTask registry(Task task) {
            if (task instanceof FixedRateTask) {
                return SchedXTaskRegistrar.this.scheduleFixedRateTask((FixedRateTask) task);
            }
            return null;
        }

        @Override
        public int supportScheduleType() {
            return 2;
        }

        @Override
        public ScheduledTask registry(SchedXDynamicTaskContext context) {
            return SchedXTaskRegistrar.this.parseFixedRateTask(context);
        }
    }

    /**
     * 固定延迟任务注册器
     *
     * @author witt
     * @see FixedDelayTask
     */
    protected class FixedDelayTaskRegister implements TaskRegister {
        @Override
        public ScheduledTask registry(Task task) {
            if (task instanceof FixedDelayTask) {
                return SchedXTaskRegistrar.this.scheduleFixedDelayTask((FixedDelayTask) task);
            }
            return null;
        }

        @Override
        public int supportScheduleType() {
            return 1;
        }

        @Override
        public ScheduledTask registry(SchedXDynamicTaskContext context) {
            return SchedXTaskRegistrar.this.parseFixedDelayTask(context);
        }
    }
}