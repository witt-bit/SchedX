package org.schedx.listen;

import lombok.AllArgsConstructor;
import org.schedx.listen.event.SchedXGroupEvent;
import org.schedx.listen.event.SchedXJobEvent;
import org.schedx.listen.event.SchedXTaskEvent;
import org.springframework.context.ApplicationEventPublisher;

/**
 * <p>将{@link SchedXListener}桥接到{@link ApplicationEventPublisher}的监听器</p>
 * <p>创建于 2025-05-05 14:49 14:49 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @see org.springframework.context.ApplicationListener
 * @see org.springframework.context.ApplicationEvent
 * @see org.springframework.context.PayloadApplicationEvent
 * @see ApplicationEventPublisher
 * @see org.springframework.context.ApplicationEventPublisherAware
 * @since 0.0.1
 */
@AllArgsConstructor
public class SchedX2ApplicationEventBridgeListener implements SchedXListener {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onGroupEvent(SchedXGroupEvent event) {
        this.eventPublisher.publishEvent(event);
    }

    @Override
    public void onJobEvent(SchedXJobEvent event) {
        this.eventPublisher.publishEvent(event);
    }

    @Override
    public void onTaskEvent(SchedXTaskEvent event) {
        this.eventPublisher.publishEvent(event);
    }
}