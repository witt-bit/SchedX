package org.schedx.util;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * <p>  </p>
 * <p>创建于 2025-06-09 23:04 23:04 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class ReflectionUtil {
    /**
     * 获取Spring源码中同名的属性值
     *
     * @param target          目标
     * @param nestedFieldName 嵌套字段名称，支持a.b.c格式
     * @return {@link X }
     */
    @SuppressWarnings("unchecked")
    public static <X> X getSameNameSpringField(Object target, String nestedFieldName) {
        // 提取Spring的内部类
        Class<?> targetClass = target.getClass();
        while (targetClass != Object.class) {
            targetClass = targetClass.getSuperclass();
            if (targetClass.getTypeName().startsWith("org.springframework.")) {
                break;
            }
        }

        Object nestedObject = target;
        for (String fieldName : nestedFieldName.split("\\.")) {
            final Field field = ReflectionUtils.findField(targetClass, fieldName);
            if (null == field) {
                throw new IllegalArgumentException("Field '" + fieldName + "' not found in class '" + targetClass + "' .");
            }
            // store the accessibility
            final boolean accessible = field.isAccessible();
            try {
                field.setAccessible(true);
                nestedObject = ReflectionUtils.getField(field, nestedObject);
                if (nestedObject == null) {
                    return null;
                }
                targetClass = field.getType();
            } finally {
                // restore the accessibility
                field.setAccessible(accessible);
            }
        }

        return (X) nestedObject;
    }
}