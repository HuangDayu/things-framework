package cn.huangdayu.things.engine.core;

/**
 * @author huangdayu
 */
public interface ThingsPropertiesEngine {


    /**
     * 获取产品配置信息对象
     *
     * @param productCode
     * @param <T>
     * @return
     */
    <T> T getProperties(String productCode);

    /**
     * 获取设备配置信息对象
     *
     * @param productCode
     * @param deviceCode
     * @param <T>
     * @return
     */
    <T> T getProperties(String productCode, String deviceCode);


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

}
