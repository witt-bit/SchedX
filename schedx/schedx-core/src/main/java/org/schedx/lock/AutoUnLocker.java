package org.schedx.lock;

/**
 * <p>自动释放锁</p>
 * <p>创建于 2025-05-04 22:10 22:10 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public interface AutoUnLocker extends AutoCloseable {

    /**
     * 不执行任何操作的{@link AutoUnLocker}
     */
    AutoUnLocker NO_OP = new AutoUnLocker() {
        @Override
        public void unlock() {
        }

        @Override
        public boolean isLocked() {
            return false;
        }
    };

    @Override
    default void close() throws Exception {
        this.unlock();
    }

    /**
     * 释放锁
     */
    void unlock();

    /**
     * 当前任务是否被锁定
     *
     * @return boolean
     */
    boolean isLocked();
}