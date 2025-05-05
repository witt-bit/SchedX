package org.schedx.adapter;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.SchedulingAwareRunnable;
import org.springframework.scheduling.support.DefaultScheduledTaskObservationConvention;
import org.springframework.scheduling.support.ScheduledTaskObservationContext;
import org.springframework.scheduling.support.ScheduledTaskObservationConvention;
import org.springframework.scheduling.support.ScheduledTaskObservationDocumentation;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * <p>对{@link org.springframework.scheduling.support.ScheduledMethodRunnable}的扩展</p>
 * <p>创建于 2025-05-05 10:22 10:22 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @see org.springframework.scheduling.support.ScheduledMethodRunnable
 * @since 0.0.1
 */
public class SchedXSpringBoot32xTaskRunnable extends SchedXTaskRunnable implements SchedulingAwareRunnable {
    protected static final ScheduledTaskObservationConvention DEFAULT_CONVENTION =
            new DefaultScheduledTaskObservationConvention();

    @Setter
    @Getter
    // @Nullable
    protected String qualifier;

    private Supplier<ObservationRegistry> observationRegistrySupplier;

    public SchedXSpringBoot32xTaskRunnable(String taskId, Object target, Method method) {
        super(taskId, target, method);
        this.observationRegistrySupplier = () -> ObservationRegistry.NOOP;
    }

    public SchedXSpringBoot32xTaskRunnable setObservationRegistrySupplier(Supplier<ObservationRegistry> observationRegistrySupplier) {
        if (null != observationRegistrySupplier) {
            this.observationRegistrySupplier = observationRegistrySupplier;
        }
        return this;
    }

    @Override
    protected Object invoke() {
        ScheduledTaskObservationContext context = new ScheduledTaskObservationContext(this.target, this.method);
        Observation observation = ScheduledTaskObservationDocumentation.TASKS_SCHEDULED_EXECUTION.observation(
                null, DEFAULT_CONVENTION,
                () -> context, this.observationRegistrySupplier.get());
        return observation.observe(() -> {
            final Object result = super.invoke();
            context.setComplete(true);
            return result;
        });
    }
}