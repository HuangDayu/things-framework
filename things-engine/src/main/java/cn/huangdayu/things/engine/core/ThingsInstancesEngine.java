package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.engine.wrapper.ThingsInstance;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsInstancesEngine {


    /**
     * 获取本实例
     *
     * @return
     */
    ThingsInstance getThingsInstance();

    /**
     * 获取提供者物模型实例
     *
     * @param productCode 产品标识
     * @param deviceCode  设备标识
     * @param identifier  功能标识
     * @return
     */
    Set<ThingsInstance> getProvideInstances(String productCode, String deviceCode, String identifier);


    /**
     * 获取消费者物模型实例
     *
     * @param productCode
     * @param deviceCode
     * @param identifier
     * @return
     */
    Set<ThingsInstance> getConsumeInstances(String productCode, String deviceCode, String identifier);

    /**
     * 更新实例
     *
     * @param thingsInstances
     * @return
     */
    Set<ThingsInstance> addInstances(Set<ThingsInstance> thingsInstances);


    /**
     * 同步所有实例
     *
     * @param thingsInstances
     * @return
     */
    Set<ThingsInstance> syncAllInstances(Set<ThingsInstance> thingsInstances);

    /**
     * 移除实例
     *
     * @param thingsInstances
     * @return
     */
    Set<ThingsInstance> removeInstances(Set<ThingsInstance> thingsInstances);


    /**
     * 按实例标识删除实例
     *
     * @param thingsInstanceCodes
     * @return
     */
    Set<ThingsInstance> removeInstancesByCodes(Set<String> thingsInstanceCodes);

    /**
     * 交换实例信息
     *
     * @param thingsInstance 请求者的实例信息
     * @return 本实例的信息
     */
    ThingsInstance exchangeInstance(ThingsInstance thingsInstance);

}
