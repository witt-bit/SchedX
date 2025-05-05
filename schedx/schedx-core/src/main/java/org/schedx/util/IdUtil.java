package org.schedx.util;

import java.util.UUID;

/**
 * <p>id工具类</p>
 * <p>创建于 2025-05-04 21:33 21:33 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public final class IdUtil {

    private IdUtil() {
    }

    public static String simpleUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}