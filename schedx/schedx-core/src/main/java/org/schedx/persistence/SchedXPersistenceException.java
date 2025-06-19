package org.schedx.persistence;

import org.schedx.SchedXException;

/**
 * <p>{@code schedx}自定义持久化相关异常，拆分以实现{@code WebExceptionHandler}的全局捕获</p>
 * <p>创建于 2025-06-15 20:24 20:24 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class SchedXPersistenceException extends SchedXException {
    private static final long serialVersionUID = -6763974668603132016L;

    public SchedXPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 缺失持久层记录
     *
     * @param condition 任务ID
     * @return {@link SchedXPersistenceException }
     */
    public static SchedXPersistenceException missingRow(String entity, String condition) {
        return new SchedXPersistenceException(entity + " '" + condition + "'record does not exist .", null);
    }
}