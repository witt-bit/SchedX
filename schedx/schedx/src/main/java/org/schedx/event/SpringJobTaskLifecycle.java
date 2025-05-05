package org.schedx.event;

/**
 * <p>  </p>
 * <p>创建于 2025-04-28 22:58 22:58 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since
 */
public interface SpringJobTaskLifecycle {

    void start(String taskId);

    void cancel(String taskId);

}