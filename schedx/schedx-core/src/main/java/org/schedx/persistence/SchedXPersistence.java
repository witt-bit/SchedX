package org.schedx.persistence;

import org.schedx.listen.event.SchedXState;
import org.schedx.persistence.support.StreamResult;

/**
 * <p>Schedx持久化接口，项目可实现该接口，完成{@link SchedXGroup}、{@link SchedXJob}和{@link SchedXTask}的持久化</p>
 * <p>创建于 2025-05-05 14:01 14:01 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public interface SchedXPersistence {

    /**
     * 初始化组
     * <ul>
     *     <li>如果组不存在，则应该创建该组，并返回创建成功的{@link SchedXGroup#getId()}</li>
     *     <li>如果组存在，则应该将此IP注册为{@link SchedXState#STARTING}，并返回{@link SchedXGroup#getIp()}可唯一辨别的{@link SchedXGroup#getId()}</li>
     * </ul>
     *
     * @param group 团体
     * @return {@link String }
     */
    String initializeGroup(SchedXGroup group);

    /**
     * 新建或查询{@link SchedXJob}详情，一个{@link org.springframework.scheduling.annotation.Scheduled}注解对应一个{@link SchedXJob}
     * <ul>
     *     <li>如果{@link SchedXJob}不存在，则新建, 并返回{@link SchedXJob#getId()}</li>
     *     <li>如果{@link SchedXJob}存在，需要将持久化后的所有值赋值到{@code job}中（后续的注册调度都将采用{@code job}）,并返回{@link SchedXJob#getId()}</li>
     * </ul>
     *
     * @param job 工作
     * @return {@link String }
     */
    String initializeOrLoadJob(SchedXJob job);

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
    String initializeOrLoadTask(SchedXTask task);

    /**
     * 获得{@code job}下所有的任务,可流式或分页返回
     * 每次调用，将{@link StreamResult#getStreamNo()} 加1后，作为下次{@code streamNo}参数，
     * 直到{@link StreamResult#hasMore()}为false，则停止调用
     *
     * @param job      所属job对象
     * @param streamNo 流编号
     * @return StreamResult<SchedXTask>
     */
    StreamResult<SchedXTask> loadJobTasks(SchedXJob job, long streamNo);

    /**
     * 加载任务详情
     * @param taskId 任务ID
     * @return {@link SchedXTask }
     */
    SchedXTask loadTask(String taskId);
}