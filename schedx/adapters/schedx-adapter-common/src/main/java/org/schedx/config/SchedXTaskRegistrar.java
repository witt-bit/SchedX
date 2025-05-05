package org.schedx.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.schedx.listen.SchedX2ApplicationEventBridgeListener;
import org.schedx.listen.SchedXListener;
import org.schedx.listen.SchedXListenerProxy;
import org.schedx.lock.NoOpTaskLocker;
import org.schedx.lock.SchedXTaskLocker;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * <p>{@link ScheduledTaskRegistrar}基本增强实现</p>
 * <p>创建于 2025-05-03 22:46 22:46 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @see ScheduledTaskRegistrar
 * @since 0.0.1
 */
@Slf4j
public class SchedXTaskRegistrar extends ScheduledTaskRegistrar {


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

    @Getter
    private final SchedXProperties schedXProperties;

    public SchedXTaskRegistrar(SchedXProperties schedXProperties) {
        this.schedXProperties = schedXProperties;
    }

    @Override
    protected void scheduleTasks() {
        if (!this.applicationReady) {
            log.trace("SchedXTaskRegistrar is not ready.");
            return;
        }
        super.scheduleTasks();
    }


    protected void applicationReady() {
        this.applicationReady = true;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.schedXProperties.isBridgeApplicationEvent()) {
            this.addListener(new SchedX2ApplicationEventBridgeListener(this.applicationEventPublisher));
        }
        super.afterPropertiesSet();
    }

    @Override
    public void destroy() {
        log.info("SchedX shutdown.");
        super.destroy();
    }

    /**
     * 设置任务锁
     *
     * @param taskLocker 任务锁实现
     * @return {@link SchedXTaskRegistrar }
     */
    public SchedXTaskRegistrar setTaskLocker(SchedXTaskLocker taskLocker) {
        if (null != taskLocker) {
            this.taskLocker = taskLocker;
        }
        return this;
    }

    public SchedXTaskRegistrar addListener(SchedXListener listener) {
        this.listener.addListener(listener);
        return this;
    }
}