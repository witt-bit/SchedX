package org.schedx.persistence;

import org.schedx.listen.event.SchedXState;

/**
 * <p>组持久化</p>
 * <p>创建于 2025-05-05 14:01 14:01 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public interface SchedXGroupPersistence {

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
    String initialize(SchedXGroup group);
}