package org.schedx;

/**
 * <p>{@code schedx}配置错误</p>
 * <p>创建于 2025-06-14 20:28 20:28 </p>
 *
 * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
 * @version v1.0
 * @since 0.0.1
 */
public class SchedXConfigurationException extends SchedXException {

    private static final long serialVersionUID = 3711872977454860589L;

    public SchedXConfigurationException(String message) {
        super(message);
    }

    /**
     * 缺少bean配置
     *
     * @param beanType bean类型
     * @return {@link SchedXConfigurationException }
     */
    public static SchedXConfigurationException missingBean(Class<?> beanType) {
        return new SchedXConfigurationException("Missing bean of type " + beanType.getName());
    }

    /**
     * 缺少配置
     *
     * @param configure 配置项目名称
     * @param depends   依赖着
     * @return {@link SchedXConfigurationException }
     */
    public static SchedXConfigurationException missingConfiguration(String configure, String depends) {
        return new SchedXConfigurationException("No '" + depends + "' " + configure + " found !");
    }

    /**
     * 错误的Bean配置
     *
     * @param configure 配置
     * @param depends   依赖项
     * @return {@link SchedXConfigurationException }
     */
    public static SchedXConfigurationException errorBeanConfiguration(String configure, String depends) {
        return new SchedXConfigurationException("Bean '" + configure + "' registry configuration error, unexpected ‘" + depends + "’ .");
    }


    /**
     * 不受支持类型
     *
     * @param type 类型
     * @return {@link SchedXConfigurationException }
     */
    public static SchedXConfigurationException unsupportedType(Class<?> type) {
        return new SchedXConfigurationException("Class '" + type + "' not expected type !");
    }
}