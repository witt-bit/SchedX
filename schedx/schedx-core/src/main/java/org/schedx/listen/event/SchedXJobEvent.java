package org.schedx.listen.event;

import java.io.Serializable;

/**
 * <p>Job事件</p>
 * <p>创建于 2025-04-28 22:59 22:59 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class SchedXJobEvent implements Serializable {

    private static final long serialVersionUID = -5607239278746700115L;

    /**
     * 工作ID
     */
    private String jobId;

    /**
     * 状态
     */
    private SchedXJobState state;

    /**
     * 触发时间
     */
    private final long time = System.nanoTime();
}