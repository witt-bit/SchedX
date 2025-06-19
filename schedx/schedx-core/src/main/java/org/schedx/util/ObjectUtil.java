package org.schedx.util;

import org.springframework.util.ObjectUtils;

/**
 * <p>对象工具类</p>
 * <p>创建于 2025-06-14 22:00 22:00 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class ObjectUtil extends ObjectUtils {

    /**
     * 如果{@code obj}为空，则取默认值{@code value}
     *
     * @param obj   对象
     * @param value 默认值
     * @return {@link T }
     */
    public static <T> T defaultIfNull(T obj, T value) {
        return obj == null ? value : obj;
    }
}