package org.schedx.persistence;

/**
 * <p>任务持久化，将在{@link SchedXJobPersistence}初始化后调度</p>
 * <p>创建于 2025-05-04 23:01 23:01 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @see SchedXJob
 * @see SchedXJobPersistence
 * @since 0.0.1
 */
public interface SchedXTaskPersistence {

    /**
     * 新建或查询{@link SchedXTask}详情
     * <ul>
     *     <li>如果{@link SchedXTask}不存在，则新建, 并返回{@link SchedXTask#getId()}</li>
     *     <li>如果{@link SchedXTask}存在，需要将持久化后的所有值赋值到{@code task}中（后续的注册调度都将采用{@code task}）,并返回{@link SchedXTask#getId()}</li>
     * </ul>
     *
     * @param task 任务
     * @return {@link String }
     */
    String initializeOrLoad(SchedXTask task);


}