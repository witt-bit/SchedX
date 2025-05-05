package org.schedx.parser;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>本地方法参数对象</p>
 * <p>创建于 2025-05-04 20:40 20:40 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
@Setter
@Getter
public class MethodArgument implements Serializable {
    private static final long serialVersionUID = -1052650681228268572L;

    /**
     * 参数名称
     */
    private String name;

    /**
     * 方法参数索引，从0开始
     */
    private int index;
}