package org.schedx.adapter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.schedx.listen.SchedXListener;
import org.schedx.listen.SchedXListenerProxy;
import org.schedx.lock.NoOpTaskLocker;
import org.schedx.lock.SchedXTaskLocker;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * <p>  </p>
 * <p>创建于 2025-05-03 22:46 22:46 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
@Slf4j
public class SchedXScheduledTaskRegistrar extends ScheduledTaskRegistrar {


    @Setter(AccessLevel.MODULE)
    private String springApplicationName;

    @Setter(AccessLevel.MODULE)
    protected ApplicationEventPublisher applicationEventPublisher;

    @Getter
    private SchedXTaskLocker taskLocker = NoOpTaskLocker.INSTANCE;

    @Getter
    private final SchedXListenerProxy listener = new SchedXListenerProxy();

    @Getter
    private volatile boolean applicationReady = false;


    @Override
    protected void scheduleTasks() {
        if (!this.applicationReady) {
            log.trace("SpringJobTaskRegistrar is not ready.");
            return;
        }
        super.scheduleTasks();
        // this.applicationEventPublisher.publishEvent(new SpringJobClientEvent(this.springApplicationName, SpringJobClientState.ONLINE));
    }


    protected void applicationReady() {
        this.applicationReady = true;
    }

    @Override
    public void destroy() {
        // this.applicationEventPublisher.publishEvent(new SpringJobClientEvent(this.springApplicationName, SpringJobClientState.OFFLINE));
        super.destroy();
    }

    /**
     * 设置任务锁
     *
     * @param taskLocker 任务锁实现
     * @return {@link SchedXScheduledTaskRegistrar }
     */
    public SchedXScheduledTaskRegistrar setTaskLocker(SchedXTaskLocker taskLocker) {
        if (null != taskLocker) {
            this.taskLocker = taskLocker;
        }
        return this;
    }

    public SchedXScheduledTaskRegistrar addListener(SchedXListener listener) {
        this.listener.addListener(listener);
        return this;
    }
}