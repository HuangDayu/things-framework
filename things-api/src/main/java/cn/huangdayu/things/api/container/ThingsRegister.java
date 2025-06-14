package cn.huangdayu.things.api.container;

/**
 * 物模型注册引擎
 *
 * @author huangdayu
 */
public interface ThingsRegister {

    /**
     * 注册
     *
     * @param thingsContainer
     */
    void register(ThingsContainer thingsContainer);

    /**
     * 注销
     *
     * @param thingsContainer
     */
    void unregister(ThingsContainer thingsContainer);


    /**
     * 注册单个物模型对象
     *
     * @param containerName
     * @param bean
     */
    void register(String containerName, Object bean);


    /**
     * 注销单个物模型对象
     *
     * @param containerName
     * @param bean
     */
    void unregister(String containerName, Object bean);
}
