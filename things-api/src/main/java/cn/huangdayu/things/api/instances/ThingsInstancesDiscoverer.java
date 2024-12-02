package cn.huangdayu.things.api.instances;

import cn.huangdayu.things.common.wrapper.ThingsInstance;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsInstancesDiscoverer {

    Set<ThingsInstance> getAllInstance();

}
