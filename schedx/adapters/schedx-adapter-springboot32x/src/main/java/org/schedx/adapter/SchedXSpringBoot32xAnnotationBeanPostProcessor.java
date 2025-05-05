package org.schedx.adapter;

import org.springframework.aop.support.AopUtils;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

import java.lang.reflect.Method;

/**
 * <p>{@link ScheduledAnnotationBeanPostProcessor}扩展增强/p>
 * <p>创建于 2025-04-27 20:41 20:41 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class SchedXSpringBoot32xAnnotationBeanPostProcessor extends SchedXAnnotationBeanPostProcessor {


    public SchedXSpringBoot32xAnnotationBeanPostProcessor() {
        super(new SchedXSpringBoot32xScheduledTaskRegistrar());
    }

    @Override
    protected Runnable createRunnable(Object target, Method method, @Nullable String qualifier) {
        Method invocableMethod = AopUtils.selectInvocableMethod(method, target.getClass());
        final SchedXSpringBoot32xTaskRunnable runnable = new SchedXSpringBoot32xTaskRunnable(null, target, invocableMethod);
        runnable.setQualifier(qualifier);
        runnable.setObservationRegistrySupplier(this.registrar::getObservationRegistry);
        super.afterCreateRunnable(runnable);
        return runnable;
    }
}