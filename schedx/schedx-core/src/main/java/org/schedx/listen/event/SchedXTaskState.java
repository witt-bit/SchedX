package org.schedx.listen.event;

/**
 * <p>{@link org.springframework.scheduling.annotation.Scheduled}标注的job作为任务状态</p>
 * <p>创建于 2025-04-28 23:04 23:04 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public enum SchedXTaskState {
    /**
     * 预备（等待被调度，暂停后恢复调度，也会发送此通知）
     */
    READY,

    /**
     * 运行中（任务正在执行中，可以通过日志跟踪进展）
     */
    RUNNING,

    /**
     * 本次调度成功
     */
    SUCCESS,

    /**
     * 本此调度失败
     */
    FAILURE,

    /**
     * 停止中（等待任务调度结束后停止）
     */
    STOPPING,

    /**
     * 停止（任务已被停止）
     */
    STOPPED;
}