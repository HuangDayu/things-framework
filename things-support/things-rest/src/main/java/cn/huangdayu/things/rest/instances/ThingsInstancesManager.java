package cn.huangdayu.things.rest.instances;

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
     * 获取所有实例
     *
     * @return
     */
    Set<ThingsInstance> getAllInstances();

    /**
     * 获取实例数量
     *
     * @return
     */
    int getInstancesSize();
}
