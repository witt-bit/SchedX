package org.schedx.lock;

import java.time.Duration;

/**
 * <p>不执行任何操作的锁</p>
 * <p>创建于 2025-05-04 22:37 22:37 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class NoOpTaskLocker implements SchedXTaskLocker {

    public static final SchedXTaskLocker INSTANCE = new NoOpTaskLocker();

    @Override
    public AutoUnLocker tryLock(String taskId, Duration waitTime, Duration leaseTime) {
        return AutoUnLocker.NO_OP;
    }

    @Override
    public boolean isLocked(String taskId) {
        return false;
    }
}