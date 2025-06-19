package org.schedx.listen;

import org.schedx.listen.event.SchedXGroupEvent;
import org.schedx.listen.event.SchedXJobEvent;
import org.schedx.listen.event.SchedXTaskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

/**
 * <p>{@link SchedXListener}代理适配器，允许多个监听器按顺序监听</p>
 * <p>创建于 2025-05-05 14:33 14:33 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class SchedXListenerProxy implements SchedXListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedXListenerProxy.class);

    /**
     * 监听者列表
     */
    private final Set<SchedXListener> listeners = new ConcurrentSkipListSet<>();

    /**
     * 像Schedx添加一个监听者实现
     *
     * @param listener 听众
     */
    public void addListener(SchedXListener listener) {
        if (null != listener) {
            this.listeners.add(listener);
        }
    }

    @Override
    public void onGroupEvent(SchedXGroupEvent event) {
        this.doListen(listener -> listener.onGroupEvent(event));
    }

    @Override
    public void onJobEvent(SchedXJobEvent event) {
        this.doListen(listener -> listener.onJobEvent(event));
    }

    @Override
    public void onTaskEvent(SchedXTaskEvent event) {
        this.doListen(listener -> listener.onTaskEvent(event));
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE;
    }

    /**
     * 监听
     *
     * @param invoker 调用
     */
    private void doListen(Consumer<SchedXListener> invoker) {
        for (SchedXListener listener : this.listeners) {
            try {
                invoker.accept(listener);
            } catch (Throwable ex) {
                LOGGER.error("Invoke listener '{}' error.", listener.getClass().getName(), ex);
            }
        }
    }
}