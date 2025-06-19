package org.schedx.config;

import lombok.Setter;
import org.schedx.SchedXTaskException;
import org.schedx.persistence.SchedXPersistence;
import org.schedx.util.ReflectionUtil;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

/**
 * <p> {@link ScheduledAnnotationBeanPostProcessor}扩展增强</p>
 * <p>创建于 2025-04-27 20:41 20:41 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 */
public class SchedXAnnotationBeanPostProcessor extends ScheduledAnnotationBeanPostProcessor
        implements ApplicationEventPublisherAware, ApplicationRunner {

    /**
     * 冗余{@code ScheduledAnnotationBeanPostProcessor.nonAnnotatedClasses},因属性访问修饰符为{@code private final}
     */
    @NonNull
    protected final Set<Class<?>> nonAnnotatedClasses;

    /**
     * 冗余{@code ScheduledAnnotationBeanPostProcessor.scheduledTasks},因属性访问修饰符为{@code private final}
     */
    @NonNull
    protected final Map<Object, Set<ScheduledTask>> scheduledTasks;

    /**
     * 冗余{@code ScheduledAnnotationBeanPostProcessor.registrar},因属性访问修饰符为{@code private final}
     */
    @NonNull
    protected final SchedXTaskRegistrar registrar;

    /**
     * 配置属性
     */
    @NonNull
    protected final SchedXProperties properties;

    @Setter
    protected ObjectFactory<SchedXPersistence> groupPersistenceObjectFactory;

    /**
     * 冗余{@link ScheduledAnnotationBeanPostProcessor#setEmbeddedValueResolver(StringValueResolver)},
     * 因属性{@code ScheduledAnnotationBeanPostProcessor.embeddedValueResolver}访问修饰符为{@code private}
     */
    protected StringValueResolver embeddedValueResolver;


    public SchedXAnnotationBeanPostProcessor(SchedXProperties properties) {
        this(new SchedXTaskRegistrar(properties));
    }

    public SchedXAnnotationBeanPostProcessor(@NonNull SchedXTaskRegistrar registrar) {
        super(registrar);
        this.registrar = registrar;
        this.properties = registrar.getSchedXProperties();
        // this.superInstanceBeanWrapper = new BeanWrapperImpl(this);
        final Set<Class<?>> nonAnnotatedClasses = ReflectionUtil.getSameNameSpringField(this, "nonAnnotatedClasses");
        if (null == nonAnnotatedClasses) {
            throw SchedXTaskException.never();
        }
        this.nonAnnotatedClasses = nonAnnotatedClasses;
        final Map<Object, Set<ScheduledTask>> scheduledTasks = ReflectionUtil.getSameNameSpringField(this, "scheduledTasks");
        if (null == scheduledTasks) {
            throw SchedXTaskException.never();
        }
        this.scheduledTasks = scheduledTasks;
        this.registrar.setBeanTasks(this.scheduledTasks);
    }


    @Override
    @SuppressWarnings("NullableProblems")
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof AopInfrastructureBean || bean instanceof TaskScheduler ||
                bean instanceof ScheduledExecutorService) {
            // Ignore AOP infrastructure such as scoped proxies.
            return bean;
        }

        final Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (this.nonAnnotatedClasses.contains(targetClass)) {
            return bean;
        }

        boolean hasScheduledMethod = this.parseAnnotation(bean, beanName, targetClass);
        if (!hasScheduledMethod) {
            this.nonAnnotatedClasses.add(targetClass);
        }
        return bean;
    }

    /**
     * 解析{@link Scheduled}和{@link StringValueResolverchedules}注解
     *
     * @param bean        豆
     * @param beanName    豆名称
     * @param targetClass 目标类
     * @return boolean
     */
    protected boolean parseAnnotation(Object bean, String beanName, Class<?> targetClass) {
        Map<Method, Set<Scheduled>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                (MethodIntrospector.MetadataLookup<Set<Scheduled>>) method -> {
                    Set<Scheduled> scheduledAnnotations = AnnotatedElementUtils.getMergedRepeatableAnnotations(
                            method, Scheduled.class, Schedules.class);
                    return (!scheduledAnnotations.isEmpty() ? scheduledAnnotations : null);
                });
        if (annotatedMethods.isEmpty()) {
            if (logger.isTraceEnabled()) {
                logger.trace("No @Scheduled annotations found on bean class: " + targetClass);
            }

            return false;
        }

        // Non-empty set of methods
        annotatedMethods.forEach((method, scheduledAnnotations) ->
                scheduledAnnotations.forEach(scheduled -> this.processScheduled(scheduled, method, bean)));
        if (logger.isTraceEnabled()) {
            logger.trace(annotatedMethods.size() + " @Scheduled methods processed on bean '" + beanName +
                    "': " + annotatedMethods);
        }

        return true;
    }


    @Override
    @SuppressWarnings("NullableProblems")
    protected void processScheduled(Scheduled scheduled, Method method, Object bean) {
        try {
            final Runnable runnable = this.createRunnable(bean, method);
            final SchedXScheduledAnnotationContext context = new SchedXScheduledAnnotationContext(bean, method, scheduled, runnable);
            context.setEmbeddedValueResolver(this.embeddedValueResolver);
            this.processScheduledTask(context);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "Encountered invalid @Scheduled method '" + method.getName() + "': " + ex.getMessage(), ex);
        }
    }


    protected void processScheduledTask(SchedXScheduledAnnotationContext context) {
        final Set<ScheduledTask> tasks = this.registrar.parseScheduledTasks(context);
        // Check whether we had any attribute set
        if (tasks.isEmpty()) {
            String errorMessage = "Exactly one of the 'cron', 'fixedDelay' or 'fixedRate' attributes is required";
            throw new IllegalArgumentException(errorMessage);
        }
        Object bean = context.getBean();

        // Finally register the scheduled tasks
        synchronized (this.scheduledTasks) {
            Set<ScheduledTask> regTasks = this.scheduledTasks.computeIfAbsent(bean, key -> new LinkedHashSet<>(4));
            regTasks.addAll(tasks);
        }
    }


    @Override
    public void afterSingletonsInstantiated() {
        // Remove resolved singleton classes from cache
        this.nonAnnotatedClasses.clear();
        super.afterSingletonsInstantiated();
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void postProcessBeforeDestruction(Object bean, String beanName) {
        super.postProcessBeforeDestruction(bean, beanName);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    protected Runnable createRunnable(Object target, Method method) {
        Method invocableMethod = AopUtils.selectInvocableMethod(method, target.getClass());
        final SchedXTaskRunnable runnable = new SchedXTaskRunnable(null, target, invocableMethod);

        // 客制化
        this.afterCreateRunnable(runnable);
        return runnable;
    }

    /**
     * 创建{@link SchedXTaskRunnable}
     *
     * @param runnable 实例
     */
    protected void afterCreateRunnable(SchedXTaskRunnable runnable) {
        runnable.setTaskLocker(this.registrar.getTaskLocker());
        runnable.setListener(this.registrar.getListener());
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("SchedX ready .");
        this.registrar.applicationReady();
        this.registrar.scheduleTasks();
    }


    @Override
    @SuppressWarnings("NullableProblems")
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.registrar.setApplicationEventPublisher(applicationEventPublisher);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void setApplicationContext(ApplicationContext applicationContext) {
        super.setApplicationContext(applicationContext);
        this.registrar.setSpringApplicationName(applicationContext.getId());
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        super.setEmbeddedValueResolver(resolver);
        this.embeddedValueResolver = resolver;
    }
}