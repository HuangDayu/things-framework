package cn.huangdayu.things.cloud.instances;

import cn.huangdayu.things.engine.wrapper.ThingsInstance;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsInstancesGetter {

    Set<ThingsInstance> getAllInstance();

}
