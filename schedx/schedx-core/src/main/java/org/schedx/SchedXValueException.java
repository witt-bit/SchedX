package org.schedx;

import lombok.Getter;

/**
 * <p>{@code schedx}属性值相关的异常</p>
 * <p>创建于 2025-06-14 21:41 21:41 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class SchedXValueException extends SchedXException {

    private static final long serialVersionUID = -1133545874138586639L;

    @Getter
    private final String property;

    @Getter
    private final String value;

    public SchedXValueException(String property, String value, String message, Throwable cause) {
        super(message, cause);
        this.value = value;
        this.property = property;
    }

    public SchedXValueException(String property, String value, String message) {
        this(property, value, message, null);
    }

    public static SchedXValueException missingValue(String property) {
        return new SchedXValueException(property, null, "Missing value for property '" + property + "' !");
    }

    public static SchedXValueException illegalValue(String property, Object value) {
        return new SchedXValueException(property, value.toString(), "Illegal value '" + value + "' for property '" + property + "' !");
    }
}