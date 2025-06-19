package org.schedx;

import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.concurrent.ExecutionException;

/**
 * <p>{@link org.schedx.persistence.SchedXTask}相关异常</p>
 * <p>创建于 2025-06-14 15:54 15:54 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class SchedXTaskException extends SchedXException {
    private static final long serialVersionUID = 2213535267454834104L;

    @Getter
    @Nullable
    private final String jobId;

    @Getter
    private final String taskId;

    public SchedXTaskException(@Nullable String jobId, String taskId, String message, Throwable ex) {
        super(message, ex);
        this.jobId = jobId;
        this.taskId = taskId;
    }

    public SchedXTaskException(@Nullable String jobId, String taskId, String message) {
        super(message);
        this.jobId = jobId;
        this.taskId = taskId;
    }

    /**
     * 任务缺失或状态不匹配异常
     *
     * @param jobId  工作ID
     * @param taskId 任务ID
     * @return {@link SchedXTaskException }
     */
    public static SchedXTaskException missingTaskOrStateRefreshed(String jobId, String taskId) {
        final String message = "Missing task '" + taskId + "' or task state refreshed .";
        return new SchedXTaskException(jobId, taskId, message, null);
    }

    /**
     * 任务缺失异常
     *
     * @param jobId  工作ID
     * @param taskId 任务ID
     * @return {@link SchedXTaskException }
     */
    public static SchedXTaskException missingTask(String jobId, String taskId) {
        final String message = "Missing task '" + taskId + "' .";
        return new SchedXTaskException(jobId, taskId, message, null);
    }

    public static SchedXTaskException missingJob(@NonNull String jobId, @Nullable String taskId) {
        final String message = "Missing job '" + jobId + "' .";
        return new SchedXTaskException(jobId, taskId, message);
    }

    /**
     * 任务执行错误
     *
     * @param jobId  工作ID
     * @param taskId 任务ID
     * @param ex     异常
     * @return {@link SchedXTaskException }
     */
    public static SchedXTaskException executeError(@NonNull String jobId, String taskId, Throwable ex) {
        final Throwable causeException;
        if (ex instanceof ExecutionException) {
            causeException = ex.getCause();
        } else {
            causeException = ex;
        }
        final String message = "Execute task error, jobId:" + jobId + ", taskId:" + taskId;
        return new SchedXTaskException(jobId, taskId, message, causeException);
    }
}