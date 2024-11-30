package cn.huangdayu.things.discovery.instances;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.instances.ThingsInstances;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.properties.ThingsProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;

import java.util.Set;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsServerInstancesGetter extends ThingsRestfulInstancesGetter implements ThingsInstancesGetter {

    private final ThingsProperties thingsProperties;

    public ThingsServerInstancesGetter(ThingsInstances thingsInstancesEngine, ThingsProperties thingsProperties, ThingsEndpointFactory thingsEndpointFactory) {
        super(thingsInstancesEngine, thingsEndpointFactory);
        this.thingsProperties = thingsProperties;
    }

    @Override
    public Set<ThingsInstance> getAllInstance() {
        return getAllThingsInstance(thingsProperties.getServers());
    }
}
