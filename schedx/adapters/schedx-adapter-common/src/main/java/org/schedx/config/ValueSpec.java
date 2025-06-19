package org.schedx.config;

import lombok.Getter;

/**
 * <p>一个值容器，用于解决无法区分值为{@code null}和未赋值的场景</p>
 * <p>创建于 2025-06-04 21:27 21:27 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class ValueSpec<T> {

    private T value;

    /**
     * 是否获得该值
     */
    @Getter
    private boolean resolved = false;

    private ValueSpec(T value) {
        this.value = value;
    }

    /**
     * 获取值
     *
     * @return {@link T }
     */
    public final T value() {
        return this.value;
    }

    /**
     * 如果{@link #isNullValue()},则返回{@code defaultValue}
     *
     * @param defaultValue 默认值
     * @return {@link T }
     */
    public final T orElseIfNull(T defaultValue) {
        if (this.isNullValue()) {
            return defaultValue;
        }
        return this.value;
    }

    /**
     * 如果{@link #isUnresolved()},则返回{@code defaultValue}
     *
     * @param defaultValue 默认值
     * @return {@link T }
     */
    public final T orElseIfUnresolved(T defaultValue) {
        if (this.isUnresolved()) {
            return defaultValue;
        }
        return this.value;
    }

    /**
     * 值是否为空
     *
     * @return boolean
     */
    public boolean isNullValue() {
        return this.value == null;
    }

    /**
     * 值是否不为空
     *
     * @return boolean
     */
    public boolean isNoNullValue() {
        return this.value != null;
    }

    /**
     * 使用{@code value}解决当前的{@link ValueSpec}
     *
     * @param value 价值
     * @return {@link ValueSpec }<{@link T }>
     */
    public ValueSpec<T> resolve(T value) {
        this.value = value;
        this.resolved = true;
        return this;
    }

    /**
     * 是否尚未解决
     *
     * @return boolean
     */
    public boolean isUnresolved() {
        return this.resolved;
    }

    /**
     * 返回一个值描述对象
     *
     * @param value 价值
     * @return {@link ValueSpec }<{@link T }>
     */
    public static <T> ValueSpec<T> of(T value) {
        final ValueSpec<T> valueSpec = new ValueSpec<>(value);
        valueSpec.resolved = true;
        return valueSpec;
    }

    public static <T> ValueSpec<T> empty() {
        return new ValueSpec<>(null);
    }
}