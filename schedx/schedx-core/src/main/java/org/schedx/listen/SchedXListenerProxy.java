package org.schedx.listen;

import org.schedx.listen.event.SchedXGroupEvent;
import org.schedx.listen.event.SchedXJobEvent;
import org.schedx.listen.event.SchedXTaskEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>{@link SchedXListener}代理适配器，允许多个监听器按顺序监听</p>
 * <p>创建于 2025-05-05 14:33 14:33 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class SchedXListenerProxy implements SchedXListener {

    /**
     * 监听者列表
     */
    private final List<SchedXListener> listeners = new ArrayList<>();

    public void addListener(SchedXListener listener) {
        if (null != listener) {
            this.listeners.add(listener);
            listeners.sort(null);
        }
    }

    @Override
    public void onGroupEvent(SchedXGroupEvent event) {
        listeners.forEach(listener -> listener.onGroupEvent(event));
    }

    @Override
    public void onJobEvent(SchedXJobEvent event) {
        listeners.forEach(listener -> listener.onJobEvent(event));
    }

    @Override
    public void onTaskEvent(SchedXTaskEvent event) {
        listeners.forEach(listener -> listener.onTaskEvent(event));
    }
}