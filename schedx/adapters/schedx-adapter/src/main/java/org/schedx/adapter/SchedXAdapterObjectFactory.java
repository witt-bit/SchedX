package org.schedx.adapter;

import org.schedx.config.SchedXAnnotationBeanPostProcessor;
import org.schedx.config.SchedXProperties;
import org.schedx.config.SchedXSpringBoot32XTaskRegistrar;
import org.schedx.config.SchedXSpringBoot32xAnnotationBeanPostProcessor;
import org.schedx.config.SchedXSpringBoot32xTaskRunnable;
import org.schedx.config.SchedXTaskRegistrar;
import org.schedx.config.SchedXTaskRunnable;
import org.schedx.util.SemanticVersion;
import org.springframework.boot.SpringBootVersion;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.lang.reflect.Method;

/**
 * <p>SchedX核心对象工厂，主要解决跨Spring版本兼容性问题</p></p>
 * <p>创建于 2025-05-03 21:03 21:03 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public final class SchedXAdapterObjectFactory {

    /**
     * 创建{@link ScheduledAnnotationBeanPostProcessor}的拓展实现
     *
     * @return {@link SchedXAnnotationBeanPostProcessor }
     */
    public static SchedXAnnotationBeanPostProcessor createAnnotationBeanPostProcessor(SchedXProperties properties) {
        final String springBootVersion = SpringBootVersion.getVersion();
        if (SemanticVersion.parse(springBootVersion).isLessThanOrEqual("3.2.0")) {
            return new SchedXSpringBoot32xAnnotationBeanPostProcessor(properties);
        }

        return new SchedXAnnotationBeanPostProcessor(properties);
    }

    /**
     * 创建{@link ScheduledTaskRegistrar}的拓展实现
     *
     * @return {@link SchedXTaskRegistrar }
     */
    public static SchedXTaskRegistrar createTaskRegistrar(SchedXProperties properties) {
        final String springBootVersion = SpringBootVersion.getVersion();
        if (SemanticVersion.parse(springBootVersion).isLessThanOrEqual("3.2.0")) {
            return new SchedXSpringBoot32XTaskRegistrar(properties);
        }

        return new SchedXTaskRegistrar(properties);
    }

    /**
     * 创建{@link org.springframework.scheduling.support.ScheduledMethodRunnable}的拓展实现,对其拓展
     *
     * @return {@link SchedXTaskRunnable }
     */
    public static SchedXTaskRunnable createTaskRegistrar(String taskId, Object target, Method method) {
        final String springBootVersion = SpringBootVersion.getVersion();
        if (SemanticVersion.parse(springBootVersion).isLessThanOrEqual("3.2.0")) {
            return new SchedXSpringBoot32xTaskRunnable(taskId, target, method);
        }

        return new SchedXTaskRunnable(taskId, target, method);
    }
}