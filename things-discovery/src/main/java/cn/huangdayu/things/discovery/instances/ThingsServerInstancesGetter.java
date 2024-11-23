package cn.huangdayu.things.discovery.instances;

import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.properties.ThingsEngineProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;

import java.util.Set;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsServerInstancesGetter extends ThingsRestfulInstancesGetter implements ThingsInstancesGetter {

    private final ThingsEngineProperties thingsEngineProperties;

    public ThingsServerInstancesGetter(ThingsInstancesManager thingsInstancesEngine, ThingsEngineProperties thingsEngineProperties) {
        super(thingsInstancesEngine);
        this.thingsEngineProperties = thingsEngineProperties;
    }

    @Override
    public Set<ThingsInstance> getAllInstance() {
        return getAllThingsInstance(thingsEngineProperties.getServers());
    }
}
