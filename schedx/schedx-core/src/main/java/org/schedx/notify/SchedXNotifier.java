package org.schedx.notify;

/**
 * <p>{@link org.schedx.persistence.SchedXJob}和{@link org.schedx.persistence.SchedXTask}变更通知接口，从持久层->{@code schedX}</p>
 * <p>创建于 2025-05-05 14:59 14:59 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public interface SchedXNotifier {

    // ------------------------------------- task  -------------------------------------

    /**
     * <p>立即执行一次任务。<br/>
     * 注意: 调度时，如果任务正在运行，将受到{@link org.schedx.lock.SchedXTaskLocker}的锁定保护</p>
     *
     * @param taskId         任务ID
     * @param callParameters 调用参数
     */
    void executeOnceTask(String taskId, String callParameters);

    /**
     * 从{@link org.schedx.persistence.SchedXJob}派生子任务{@link org.schedx.persistence.SchedXTask}
     *
     * @param jobId     工作ID
     * @param newTaskId 新任务的id
     */
    void forkTaskWithJob(String jobId, String newTaskId);

    /**
     * 修改任务, 收到此事件后，将重新加载该任务
     *
     * @param taskId 任务ID
     */
    void modifyTask(String taskId);

    /**
     * registrar
     * 终止任务
     * <ul>
     * <li>如果任务正在等待调度，将直接暂停</li>
     * <li>如果任务正在运行中，{@code interrupt}值为{@code true} 将强制打断，否则将在本次运行结束后停止</li>
     * </ul>
     *
     * @param taskId    任务ID
     * @param interrupt 打断
     * @see Thread#interrupt()
     */
    void termTask(String taskId, boolean interrupt);

    /**
     * 恢复任务执行
     *
     * @param taskId 任务ID
     */
    void resumeTask(String taskId);
}