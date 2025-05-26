package cn.huangdayu.things.rest.instances;

import cn.huangdayu.things.common.wrapper.ThingsInstance;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsInstancesDiscoverer {

    /**
     * 发现所有实例
     * @return
     */
    Set<ThingsInstance> allInstance();
}
