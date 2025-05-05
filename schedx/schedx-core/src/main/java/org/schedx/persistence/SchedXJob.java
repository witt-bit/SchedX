package org.schedx.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * <p></p>
 * <p>创建于 2025-05-04 23:17 23:17 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
@Getter
@Setter
@NoArgsConstructor
public class SchedXJob implements Serializable {
    private static final long serialVersionUID = 4438786643997972665L;

    /**
     * jobId
     */
    private String id;

    /**
     * job依赖的Java方法权限定名
     */
    private String jobReference;

    /**
     * 发起调度的Bean实例
     */
    private Object bean;

    /**
     * 发起调度的方法实例
     */
    private Method method;

    /**
     * 默认运行参数
     */
    private String defaultParams;

    /**
     * 删除
     */
    private boolean deleted;
}