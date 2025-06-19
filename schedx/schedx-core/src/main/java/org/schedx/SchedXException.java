package org.schedx;

/**
 * <p>{@code schedx}相关异常</p>
 * <p>创建于 2025-06-14 15:52 15:52 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class SchedXException extends RuntimeException{
    private static final long serialVersionUID = 170966774100164258L;

    public SchedXException(String message) {
        super(message);
    }

    public SchedXException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 这是一个永远不会发生的异常
     *
     * @return {@link SchedXException }
     */
    public static SchedXException never() {
        return new SchedXException("NeverBeHappened Error, if you see me, 😮 no ! you shouldn't see me.");
    }
}