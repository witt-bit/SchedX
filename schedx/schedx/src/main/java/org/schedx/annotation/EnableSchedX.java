package org.schedx.annotation;

import org.springframework.context.annotation.Import;
import org.schedx.config.SchedXAutoConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>对{@link org.springframework.scheduling.annotation.EnableScheduling}的增强  </p>
 * <p>创建于 2025-04-27 20:45 20:45 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SchedXAutoConfiguration.class)
@Documented
public @interface EnableSchedX {
}