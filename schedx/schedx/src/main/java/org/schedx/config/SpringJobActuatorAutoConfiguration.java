package org.schedx.config;

import org.schedx.adapter.SchedXAdapterObjectFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.TaskManagementConfigUtils;

/**
 * <p>  </p>
 * <p>创建于 2025-04-27 20:59 20:59 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since
 */
@AutoConfigureBefore(ScheduledAnnotationBeanPostProcessor.class)
@AutoConfiguration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class SpringJobActuatorAutoConfiguration {

    @Bean(name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ScheduledAnnotationBeanPostProcessor scheduledAnnotationProcessor() {
        return SchedXAdapterObjectFactory.createAnnotationBeanPostProcessor();
    }
}