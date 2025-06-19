package org.schedx.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>{@code org.springframework.boot.autoconfigure.task.TaskSchedulingProperties}的增强配置</p>
 * <p>创建于 2025-05-05 20:30 20:30 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
@ConfigurationProperties("schedx")
@Setter
@Getter
@NoArgsConstructor
public class SchedXProperties {

    /**
     * 将{@link org.schedx.listen.SchedXListener}事件桥接到{@link org.springframework.context.ApplicationEventPublisher}
     */
    private boolean bridgeApplicationEvent = false;

    /**
     * 启用异步注册任务
     */
    private boolean asyncRegisterTask = true;

    /**
     * trace相关配置
     */
    private Tracing tracing = new Tracing();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Tracing {

        /**
         * 是否启用trace
         */
        private boolean enabled = true;

        /**
         * {@code MDC}中traceId的key名称
         */
        private String tracingKey = "traceId";
    }
}