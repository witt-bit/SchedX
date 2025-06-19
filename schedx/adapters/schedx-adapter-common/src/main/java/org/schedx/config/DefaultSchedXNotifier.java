package org.schedx.config;

import lombok.AllArgsConstructor;
import org.schedx.SchedXTaskException;
import org.schedx.notify.SchedXNotifier;
import org.schedx.util.CollectionUtil;
import org.springframework.scheduling.config.ScheduledTask;

import java.util.List;

/**
 * <p>默认{@link SchedXNotifier}实现</p>
 * <p>创建于 2025-06-14 15:36 15:36 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
@AllArgsConstructor
public class DefaultSchedXNotifier implements SchedXNotifier {

    private final SchedXTaskRegistrar registrar;

    @Override
    public void executeOnceTask(String taskId, String callParameters) {
        final ScheduledTask scheduledTask = registrar.getSchedXTasks().get(taskId);
        if (null == scheduledTask) {
            throw SchedXTaskException.missingTaskOrStateRefreshed(null, taskId);
        }
        this.registrar.executeTask(scheduledTask.getTask(), callParameters);
    }

    @Override
    public void forkTaskWithJob(String jobId, String newTaskId) {
        final List<ScheduledTask> jobTasks = this.registrar.getJobs().get(jobId);
        if (CollectionUtil.isEmpty(jobTasks)) {
            throw SchedXTaskException.missingJob(jobId, newTaskId);
        }

        final ScheduledTask candidateTask = CollectionUtil.findFirstMatch(jobTasks, task -> null != this.registrar.getTaskRunnable(task));
        if (null == candidateTask) {
            // 不可能存在空的情况
            throw SchedXTaskException.never();
        }
        this.registrar.forkTask(candidateTask.getTask(), newTaskId);
    }

    @Override
    public void modifyTask(String taskId) {
        this.registrar.reload(taskId);
    }

    @Override
    public void termTask(String taskId, boolean interrupt) {
        final ScheduledTask scheduledTask = registrar.getSchedXTasks().get(taskId);
        if (null == scheduledTask) {
            throw SchedXTaskException.missingTaskOrStateRefreshed(null, taskId);
        }
        this.registrar.cancelScheduledTask(scheduledTask, interrupt);
    }

    @Override
    public void resumeTask(String taskId) {
        final ScheduledTask scheduledTask = registrar.getSchedXTasks().get(taskId);
        if (null == scheduledTask) {
            throw SchedXTaskException.missingTaskOrStateRefreshed(null, taskId);
        }
        this.registrar.resumeTask(scheduledTask);
    }
}