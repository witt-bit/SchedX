package org.schedx.listen.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * <p>  </p>
 * <p>创建于 2025-05-01 17:50 17:50 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
@Getter
@AllArgsConstructor
public class SchedXGroupEvent implements Serializable {

    private static final long serialVersionUID = -2138332732039506028L;

    /**
     * group主键
     */
    private String id;

    /**
     * 客户端注册IP地址
     */
    private String ip;

    /**
     * 状态
     */
    private SchedXState state;

    /**
     * 触发时间
     */
    private final long time = System.nanoTime();


}