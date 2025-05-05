package org.schedx.listen;

import org.schedx.listen.event.SchedXGroupEvent;
import org.schedx.listen.event.SchedXJobEvent;
import org.schedx.listen.event.SchedXTaskEvent;

/**
 * <p>SchedX运行时事件监听器</p>
 * <p>创建于 2025-05-05 12:14 12:14 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public interface SchedXListener extends Comparable<SchedXListener> {

    /**
     * 优先级, 数字越小优先级越高
     *
     * @return int
     */
    default int priority() {
        return 0;
    }

    @Override
    default int compareTo(SchedXListener o) {
        return Integer.compare(this.priority(), o.priority());
    }

    /**
     * 组事件
     *
     * @param event 事件
     */
    void onGroupEvent(SchedXGroupEvent event);

    /**
     * job事件
     *
     * @param event 事件
     */
    void onJobEvent(SchedXJobEvent event);

    /**
     * Task  事件
     *
     * @param event 事件
     */
    void onTaskEvent(SchedXTaskEvent event);
}