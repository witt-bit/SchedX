package org.schedx.config;

import lombok.AllArgsConstructor;
import org.schedx.adapter.SchedXAdapterObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.SchedulingConfiguration;
import org.springframework.scheduling.config.TaskManagementConfigUtils;

import java.time.Duration;

/**
 * <p>{@code schedx}自动配置</p>
 * <p>创建于 2025-04-27 20:59 20:59 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
@AutoConfigureBefore({SchedulingConfiguration.class, TaskSchedulingAutoConfiguration.class})
@AutoConfiguration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@EnableConfigurationProperties(SchedXProperties.class)
@AllArgsConstructor
public class SchedXAutoConfiguration implements EnvironmentAware {

    /**
     * {@link TaskSchedulingProperties.Pool#getSize()}的配置key
     */
    private static final String SCHEDULING_POOL_SIZE_PROPERTY_KEY = "spring.task.scheduling.pool.size";

    private static final Logger log = LoggerFactory.getLogger("SchedXConfiguration");

    private final TaskSchedulingProperties taskSchedulingProperties;

    @Bean(name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ScheduledAnnotationBeanPostProcessor scheduledAnnotationProcessor(SchedXProperties properties) {
        return SchedXAdapterObjectFactory.createAnnotationBeanPostProcessor(properties);
    }

    @Override
    public void setEnvironment(Environment environment) {
        // 等待调度任务结束时间
        final TaskSchedulingProperties.Shutdown shutdown = taskSchedulingProperties.getShutdown();
        final boolean awaitTermination = shutdown.isAwaitTermination();
        if (!awaitTermination) {
            shutdown.setAwaitTermination(true);
            shutdown.setAwaitTerminationPeriod(Duration.ofSeconds(30L));
            log.info("taskSchedulerPool autoConfigure awaitTermination {}s.", shutdown.getAwaitTerminationPeriod().getSeconds());
        }

        final Integer configurablePoolSize = environment.getProperty(SCHEDULING_POOL_SIZE_PROPERTY_KEY, Integer.class);
        if (null != configurablePoolSize) {
            return;
        }

        // 覆盖默认poolSize
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        final int poolSize = Math.min(availableProcessors - 1, 2);
        taskSchedulingProperties.getPool().setSize(poolSize);
        log.info("taskSchedulerPool autoConfigure poolSize {}.", poolSize);
    }
}