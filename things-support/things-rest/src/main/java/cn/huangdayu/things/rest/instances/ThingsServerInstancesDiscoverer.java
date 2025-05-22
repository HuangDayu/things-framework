package cn.huangdayu.things.rest.instances;

import cn.huangdayu.things.api.infrastructure.ThingsPropertiesService;
import cn.huangdayu.things.api.instances.ThingsInstancesDiscoverer;
import cn.huangdayu.things.api.instances.ThingsInstancesDslManager;
import cn.huangdayu.things.api.instances.ThingsInstancesRegister;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.huangdayu.things.rest.endpoint.ThingsEndpointFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class ThingsServerInstancesDiscoverer extends ThingsBaseInstancesDiscoverer implements ThingsInstancesDiscoverer, ThingsInstancesRegister {

    private final ThingsPropertiesService thingsConfigService;
    private final ThingsEndpointFactory thingsEndpointFactory;
    private final ThingsInstancesDslManager thingsInstancesDslManager;


    public ThingsServerInstancesDiscoverer(ThingsPropertiesService thingsConfigService,
                                           ThingsEndpointFactory thingsEndpointFactory,
                                           ThingsInstancesDslManager thingsInstancesDslManager) {
        super(thingsConfigService, thingsEndpointFactory, thingsInstancesDslManager);
        this.thingsConfigService = thingsConfigService;
        this.thingsEndpointFactory = thingsEndpointFactory;
        this.thingsInstancesDslManager = thingsInstancesDslManager;
    }

    @Override
    public Set<ThingsInstance> allInstance() {
        return getAllThingsInstance(thingsConfigService.getProperties().getServers());
    }

    @Override
    public void register(ThingsInstance thingsInstance) {

    }


}
