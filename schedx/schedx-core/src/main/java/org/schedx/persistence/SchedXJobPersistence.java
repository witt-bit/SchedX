package org.schedx.persistence;

/**
 * <p>{@link org.springframework.scheduling.annotation.Scheduled}持久化</p>
 * <p>创建于 2025-05-04 23:00 23:00 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public interface SchedXJobPersistence {

    /**
     * 新建或查询{@link SchedXJob}详情
     * <ul>
     *     <li>如果{@link SchedXJob}不存在，则新建, 并返回{@link SchedXJob#getId()}</li>
     *     <li>如果{@link SchedXJob}存在，需要将持久化后的所有值赋值到{@code job}中（后续的注册调度都将采用{@code job}）,并返回{@link SchedXJob#getId()}</li>
     * </ul>
     *
     * @param job 工作
     * @return {@link String }
     */
    String initializeOrLoad(SchedXJob job);
}