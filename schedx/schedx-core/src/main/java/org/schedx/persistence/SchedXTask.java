package org.schedx.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>task持久化实体, 由{@link SchedXJob}派生来</p>
 * <p>创建于 2025-05-04 23:16 23:16 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
@Setter
@Getter
@NoArgsConstructor
public class SchedXTask implements Serializable {

    private static final long serialVersionUID = 819501366536679742L;

    /**
     * 任务ID
     */
    private String id;

    /**
     * job主键，来自{@link SchedXJob#getId()}
     */
    private String jobId;

    /**
     * group主键，来自{@link SchedXGroup#getId()}
     */
    private String groupId;

    /**
     * 当前任务参数，覆盖{@link SchedXJob#getDefaultParams()}
     */
    private String params;

    /**
     * 删除
     */
    private boolean deleted;

}