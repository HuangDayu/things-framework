package cn.huangdayu.things.api.instances;

import cn.huangdayu.things.common.wrapper.ThingsInstance;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsInstancesManager {

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
    Set<ThingsInstance> addAllInstances(Set<ThingsInstance> thingsInstances);

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


    /**
     * 获取所有实例
     *
     * @return
     */
    Set<ThingsInstance> getAllThingsInstances();

    /**
     * 获取实例数量
     *
     * @return
     */
    int getInstancesSize();
}
