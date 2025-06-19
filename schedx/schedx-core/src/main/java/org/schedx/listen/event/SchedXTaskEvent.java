package org.schedx.listen.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import org.schedx.util.CollectionUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

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
@AllArgsConstructor
public class SchedXTaskEvent implements Serializable {

    private static final long serialVersionUID = 8396891743032706173L;
    /**
     * 调度任务，job->task映射，一对多
     */
    private Map<String, Set<String>> tasks;

    /**
     * 任务状态
     */
    @With
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

    /**
     * 从单个job中创建{@link SchedXTaskEvent}
     *
     * @param jobId   工作ID
     * @param taskIds 任务ID
     * @return {@link SchedXTaskEvent }
     */
    public static SchedXTaskEvent fromJob(String jobId, Collection<String> taskIds) {
        final SchedXTaskEvent event = new SchedXTaskEvent();
        final Map<String, Set<String>> tasks = Collections.singletonMap(jobId, Collections.unmodifiableSet(CollectionUtil.toSet(taskIds)));
        event.setTasks(tasks);
        return event;
    }

    /**
     * 从单个任务创建{@link SchedXTaskEvent}，注意{@link #tasks}的{@link Map#values()}是不可变的
     *
     * @param jobId  工作ID
     * @param taskId 任务ID
     * @return {@link SchedXTaskEvent }
     */
    public static SchedXTaskEvent fromTask(String jobId, String taskId) {
        return fromJob(jobId, Collections.singleton(taskId));
    }


}