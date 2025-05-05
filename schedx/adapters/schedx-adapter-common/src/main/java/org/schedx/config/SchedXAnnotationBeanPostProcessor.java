package org.schedx.config;

import org.springframework.aop.support.AopUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

import java.lang.reflect.Method;

/**
 * <p> {@link ScheduledAnnotationBeanPostProcessor}扩展增强</p>
 * <p>创建于 2025-04-27 20:41 20:41 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 */
public class SchedXAnnotationBeanPostProcessor extends ScheduledAnnotationBeanPostProcessor
        implements ApplicationEventPublisherAware, ApplicationRunner {

    protected final SchedXTaskRegistrar registrar;

    protected final SchedXProperties properties;

    public SchedXAnnotationBeanPostProcessor(SchedXProperties properties) {
        this(new SchedXTaskRegistrar(properties));
    }

    public SchedXAnnotationBeanPostProcessor(SchedXTaskRegistrar registrar) {
        super(registrar);
        this.registrar = registrar;
        this.properties = registrar.getSchedXProperties();
    }

    @Override
    protected Runnable createRunnable(Object target, Method method) {
        Method invocableMethod = AopUtils.selectInvocableMethod(method, target.getClass());
        final SchedXTaskRunnable runnable = new SchedXTaskRunnable(null, target, invocableMethod);
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
    public void run(ApplicationArguments args) throws Exception {
        logger.info("SchedX ready .");
        this.registrar.applicationReady();
        this.registrar.scheduleTasks();
    }


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.registrar.setApplicationEventPublisher(applicationEventPublisher);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        super.setApplicationContext(applicationContext);
        this.registrar.setSpringApplicationName(applicationContext.getId());
    }
}