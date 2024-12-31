package cn.huangdayu.things.engine.core;

/**
 * @author huangdayu
 */
public interface ThingsProperties {


    /**
     * 获取产品配置信息对象
     *
     * @param productCode
     * @param <T>
     * @return
     */
    <T> T getPropertyEntity(String productCode);

    /**
     * 获取设备配置信息对象
     *
     * @param productCode
     * @param deviceCode
     * @param <T>
     * @return
     */
    <T> T getPropertyEntity(String productCode, String deviceCode);


    /**
     * 获取配置值
     *
     * @param productCode
     * @param deviceCode
     * @param propertyName
     * @param <T>
     * @return
     */
    <T> T getProperty(String productCode, String deviceCode, String propertyName);


    /**
     * 设置配置值
     *
     * @param productCode
     * @param deviceCode
     * @param propertyName
     * @param value
     */
    void setProperty(String productCode, String deviceCode, String propertyName, Object value);


    /**
     * 更新配置对象信息
     * @param productCode
     * @param deviceCode
     * @param properties
     */
    void updatePropertyEntity(String productCode, String deviceCode, Object properties);


    /**
     * 上报设备属性
     * @param productCode
     * @param deviceCode
     */
    void postProperty(String productCode, String deviceCode);

}
