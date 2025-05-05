package org.schedx.lock;

import java.time.Duration;

/**
 * <p>任务执行锁，保证在分布式环境下，仅有一个task在执行，避免多节点同时调度</p>
 * <p>创建于 2025-05-04 21:52 21:52 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public interface SchedXTaskLocker {

    /**
     * 尝试获得锁，{@link AutoUnLocker#isLocked()}
     *
     * @param taskId    任务ID
     * @param waitTime  等待时间
     * @param leaseTime 租用时间
     * @return {@link AutoCloseable }
     */
    AutoUnLocker tryLock(String taskId, Duration waitTime, Duration leaseTime);

    /**
     * {@code taskId}是否被锁定
     *
     * @param taskId 任务ID
     * @return boolean
     */
    boolean isLocked(String taskId);
}