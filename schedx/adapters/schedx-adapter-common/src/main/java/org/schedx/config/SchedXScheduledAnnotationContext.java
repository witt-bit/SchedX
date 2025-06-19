package org.schedx.config;

import lombok.Getter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.Method;

/**
 * <p>基于注解{@link Scheduled}的解析上下文</p>
 * <p>创建于 2025-06-04 21:04 21:04 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @see Scheduled
 * @since 0.0.1
 */
public class SchedXScheduledAnnotationContext extends SchedXDynamicTaskContext {

    /**
     * 注解
     */
    @Getter
    protected final Scheduled scheduled;

    /**
     * 基于注解的任务基础属性封装，将{@link Scheduled}所有属性转到{@link #compatibleAttributes}中，便于跨版本兼容性处理
     *
     * @param bean      bean实例
     * @param method    注解作用的方法
     * @param scheduled 注解
     * @param runnable  任务封装
     */
    public SchedXScheduledAnnotationContext(Object bean, Method method, Scheduled scheduled, Runnable runnable) {
        super(bean, method, runnable, AnnotationUtils.getAnnotationAttributes(scheduled));
        this.scheduled = scheduled;
    }

    protected  <R> R getAttributeValue(String name) {
        return super.getAttributeValue(name);
    }

    protected  <R> R getAttributeValue(String name, R defaultValue) {
        return super.getAttributeValue(name, defaultValue);
    }
}