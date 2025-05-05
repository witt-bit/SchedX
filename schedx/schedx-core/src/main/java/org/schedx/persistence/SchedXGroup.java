package org.schedx.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.schedx.listen.event.SchedXState;

import java.io.Serializable;

/**
 * <p>  </p>
 * <p>创建于 2025-05-05 13:44 13:44 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
@Getter
@Setter
@NoArgsConstructor
public class SchedXGroup implements Serializable {

    private static final long serialVersionUID = -6657792229770924648L;

    /**
     * group主键
     */
    private String id;

    /**
     * 客户端注册IP地址
     */
    private String ip;

    /**
     * 如果能获得{@code org.springframework.boot.web.context.WebServerApplicationContext#getWebServer()#getPort()}，则返回监听的端口号
     */
    private String port;

    /**
     * 状态
     */
    private SchedXState state;

    /**
     * 组名称，一般来自{@link org.springframework.context.ApplicationContext#getId()}
     */
    private String name;
}