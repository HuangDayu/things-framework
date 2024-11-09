package cn.huangdayu.things.cloud.instances;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.engine.configuration.ThingsEngineProperties;
import cn.huangdayu.things.engine.core.ThingsInstancesEngine;
import cn.huangdayu.things.engine.wrapper.ThingsInstance;

import java.util.Set;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsServerInstancesGetter extends ThingsRestfulInstancesGetter implements ThingsInstancesGetter {

    private final ThingsEngineProperties thingsEngineProperties;

    public ThingsServerInstancesGetter(ThingsInstancesEngine thingsInstancesEngine, ThingsEngineProperties thingsEngineProperties) {
        super(thingsInstancesEngine);
        this.thingsEngineProperties = thingsEngineProperties;
    }

    @Override
    public Set<ThingsInstance> getAllInstance() {
        return getAllThingsInstance(thingsEngineProperties.getServers());
    }
}
