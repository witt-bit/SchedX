package org.schedx.listen.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>{@link Runnable}Task事件</p>
 * <p>创建于 2025-05-05 12:16 12:16 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class SchedXTaskEvent {

    /**
     * 调度Job
     */
    private String jobId;

    /**
     * 调度任务主键
     */
    private String taskId;

    /**
     * 任务状态
     */
    private SchedXTaskState state;

    /**
     * 错误
     */
    private Throwable error;

    /**
     * 结果
     */
    private Object result;

    /**
     * 触发时间
     */
    private final long time = System.nanoTime();

    public SchedXTaskEvent(String jobId, String taskId) {
        this.jobId = jobId;
        this.taskId = taskId;
    }
}