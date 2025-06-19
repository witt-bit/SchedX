package org.schedx.config;

import org.springframework.aop.support.AopUtils;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.OneTimeTask;
import org.springframework.scheduling.config.ScheduledTask;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Set;

/**
 * <p>{@link ScheduledAnnotationBeanPostProcessor}扩展增强/p>
 * <p>创建于 2025-04-27 20:41 20:41 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class SchedXSpringBoot32xAnnotationBeanPostProcessor extends SchedXAnnotationBeanPostProcessor {


    public SchedXSpringBoot32xAnnotationBeanPostProcessor(SchedXProperties schedXProperties) {
        super(new SchedXSpringBoot32XTaskRegistrar(schedXProperties));
    }

    @Override
    @SuppressWarnings("NullableProblems")
    protected Runnable createRunnable(Object target, Method method, @Nullable String qualifier) {
        Method invocableMethod = AopUtils.selectInvocableMethod(method, target.getClass());
        final SchedXSpringBoot32xTaskRunnable runnable = new SchedXSpringBoot32xTaskRunnable(null, target, invocableMethod);
        runnable.setQualifier(qualifier);
        runnable.setObservationRegistrySupplier(this.registrar::getObservationRegistry);
        super.afterCreateRunnable(runnable);
        return runnable;
    }

    // @Override
    // protected Set<ScheduledTask> parseScheduledTasks(SchedXScheduledAnnotationContext context) {
    //     final Set<ScheduledTask> tasks = super.parseScheduledTasks(context);
    //     // 兜底： 至少运行一次
    //     if (tasks.isEmpty()) {
    //         this.addParsedTask(tasks, () -> this.parseOneTimeTask(context));
    //     }
    //     return tasks;
    // }

    protected ScheduledTask parseOneTimeTask(SchedXScheduledAnnotationContext context) {
        final Duration delayToUse = context.getInitialDelay().orElseIfNull(Duration.ZERO);
        return this.newOneTimeTask(context.getRunnable(), delayToUse);
    }

    /**
     * 创建{@link org.springframework.scheduling.config.OneTimeTask}的{@link ScheduledTask}
     *
     * @param runnable   可运行
     * @param delayToUse 使用延迟
     * @return {@link ScheduledTask }
     */
    protected ScheduledTask newOneTimeTask(Runnable runnable, Duration delayToUse) {
        return this.registrar.scheduleOneTimeTask(new OneTimeTask(runnable, delayToUse));
    }
}