package org.schedx.config;

import lombok.Getter;
import lombok.Setter;
import org.schedx.listen.SchedXListener;
import org.schedx.listen.event.SchedXTaskEvent;
import org.schedx.listen.event.SchedXTaskState;
import org.schedx.lock.SchedXTaskLocker;
import org.schedx.parser.MethodArgument;
import org.schedx.parser.MethodArgumentParser;
import org.schedx.parser.MethodArgumentParserRegistry;
import org.schedx.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>task的基本{@link Runnable}实现， 对{@link ScheduledMethodRunnable}的扩展</p>
 * <p>创建于 2025-05-04 19:44 19:44 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @see ScheduledMethodRunnable
 * @see SchedXAnnotationBeanPostProcessor#createRunnable(Object, Method)
 * @since 0.0.1
 */
@Getter
public class SchedXTaskRunnable implements Runnable {

    protected static final Logger log = LoggerFactory.getLogger("SchedXTaskRunnable");

    /**
     * jobId
     */
    @Setter
    protected String jobId;

    /**
     * task主键
     */
    protected String id;

    /**
     * 当前任务状态
     */
    protected volatile SchedXTaskState state;

    /**
     * 调度目标类
     */
    protected final Object target;

    /**
     * 调度方法
     */
    protected final Method method;

    /**
     * 默认的调用参数
     */
    protected String params;

    /**
     * 任务锁
     */
    @Setter
    protected SchedXTaskLocker taskLocker;

    /**
     * 监听器
     */
    @Setter
    protected SchedXListener listener;

    public SchedXTaskRunnable(String taskId, Object target, Method method) {
        this.target = target;
        this.method = method;
        this.state = SchedXTaskState.READY;
    }

    @Override
    public void run() {
        if (this.state == SchedXTaskState.STOPPING || this.state == SchedXTaskState.STOPPED) {
            return;
        }

        final SchedXTaskEvent event = new SchedXTaskEvent(this.jobId, this.id);
        Object result = null;
        try (AutoCloseable ignore = this.beforeInvoke()) {
            this.state = SchedXTaskState.RUNNING;
            event.setState(this.state);
            this.listener.onTaskEvent(event);
            result = this.invoke();
            this.afterInvoke();
            if (this.state == SchedXTaskState.RUNNING) {
                this.state = SchedXTaskState.SUCCESS;
                event.setState(this.state);
                event.setResult(result);
                this.listener.onTaskEvent(event);
            }
        } catch (Throwable e) {
            if (this.state == SchedXTaskState.RUNNING) {
                this.state = SchedXTaskState.FAILURE;
                event.setState(this.state);
                event.setResult(result);
                this.listener.onTaskEvent(event);
            }
            ReflectionUtils.rethrowRuntimeException(e);
        } finally {
            this.invokeFinally();
        }
    }

    /**
     * 在{@link #invoke()}之前执行
     *
     * @return {@link ComposeAutoCloseable }
     */
    protected ComposeAutoCloseable beforeInvoke() {
        final ComposeAutoCloseable composeAutoCloseable = new ComposeAutoCloseable();
        // MDC
        composeAutoCloseable.add(MDC.putCloseable("traceId", IdUtil.simpleUUID()));
        // lock
        composeAutoCloseable.add(this.taskLocker.tryLock(this.id, null, null));
        return composeAutoCloseable;
    }

    /**
     * 反射调用{@link #method}
     */
    protected Object invoke() {
        try {
            ReflectionUtils.makeAccessible(this.method);
            final Object[] args = this.parseArguments();
            return this.method.invoke(this.target, args);
        } catch (InvocationTargetException ex) {
            ReflectionUtils.rethrowRuntimeException(ex.getTargetException());
        } catch (IllegalAccessException ex) {
            throw new UndeclaredThrowableException(ex);
        }

        return null;
    }

    /**
     * 将在{@link #invoke()}之后执行，仅在无异常时
     */
    protected void afterInvoke() {

    }

    /**
     * 将在{@link #invoke()}之后的{@code finally}块中执行
     */
    protected void invokeFinally() {

    }

    /**
     * 使用参数解析器，解析参数
     *
     * @return {@code Object[] }
     */
    protected Object[] parseArguments() {
        if (this.params == null) {
            return null;
        }
        final Type[] parameterTypes = this.method.getGenericParameterTypes();
        if (parameterTypes.length == 0) {
            return null;
        }

        final Parameter[] parameters = this.method.getParameters();
        final Object[] args = new Object[parameterTypes.length];
        boolean allNullValue = true;
        for (int i = 0; i < parameterTypes.length; i++) {
            final Type parameterType = parameterTypes[i];
            final Parameter parameter = parameters[i];

            final MethodArgument methodArgument = new MethodArgument();
            methodArgument.setName(parameter.getName());
            methodArgument.setIndex(i);
            final MethodArgumentParser<Object> parser = MethodArgumentParserRegistry.getParser(parameterType);
            final Object value = parser.parse(this.params, methodArgument);
            args[i] = value;
            if (null != value) {
                allNullValue = false;
            }
        }

        return allNullValue ? null : args;
    }

    /**
     * 组合多个{@link AutoCloseable}的实例
     *
     * @author witt
     */
    protected static class ComposeAutoCloseable implements AutoCloseable {

        private final List<AutoCloseable> resources;

        public ComposeAutoCloseable() {
            this.resources = new ArrayList<>();
        }

        public ComposeAutoCloseable add(AutoCloseable resource) {
            this.resources.add(resource);
            return this;
        }

        @Override
        public void close() throws Exception {
            for (AutoCloseable resource : this.resources) {
                try {
                    resource.close();
                } catch (Throwable ignore) {

                }
            }
        }
    }
}