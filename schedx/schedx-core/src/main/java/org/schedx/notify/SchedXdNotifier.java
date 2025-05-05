package org.schedx.notify;

/**
 * <p>{@link org.schedx.persistence.SchedXJob}和{@link org.schedx.persistence.SchedXTask}变更通知接口，从持久层->{@code schedX}</p>
 * <p>创建于 2025-05-05 14:59 14:59 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public interface SchedXdNotifier {

    // ------------------------------------- task  -------------------------------------

    /**
     * 立即执行一次
     *
     * @param taskId 任务ID
     */
    void executeOnceTask(String taskId);

    /**
     * 修改任务, 收到此事件后，将重新加载该任务
     *
     * @param taskId 任务ID
     */
    void modifyTask(String taskId);

    /**
     * 暂停任务
     * <ul>
     *     <li>如果任务正在等待调度，将直接暂停</li>
     *     <li>如果任务正在运行中，将在本次运行结束后停止</li>
     * </ul>
     *
     * @param taskId 任务ID
     */
    void pauseTask(String taskId);

    /**
     * 恢复任务
     *
     * @param taskId 任务ID
     */
    void resumeTask(String taskId);
}