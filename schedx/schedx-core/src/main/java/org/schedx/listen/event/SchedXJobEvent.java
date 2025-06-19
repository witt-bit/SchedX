package org.schedx.listen.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>Job事件</p>
 * <p>创建于 2025-04-28 22:59 22:59 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
@Getter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SchedXJobEvent implements Serializable {

    private static final long serialVersionUID = -5607239278746700115L;

    /**
     * 工作ID
     */
    private List<String> jobIds;

    /**
     * 状态
     */
    @With
    private SchedXJobState state;

    /**
     * 触发时间
     */
    private final long time = System.nanoTime();

    public static SchedXJobEvent fromJobs(Collection<String> jobIds, SchedXJobState state) {
        if (jobIds instanceof List) {
            return new SchedXJobEvent((List<String>) jobIds, state);
        }
        return new SchedXJobEvent(new ArrayList<>(jobIds), state);
    }

    public static SchedXJobEvent fromJob(String jobId, SchedXJobState state) {
        return fromJobs(Collections.singletonList(jobId), state);
    }
}