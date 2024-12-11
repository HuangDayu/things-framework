package cn.huangdayu.things.discovery;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.instances.ThingsInstancesDiscoverer;
import cn.huangdayu.things.api.instances.ThingsInstancesDslManager;
import cn.huangdayu.things.api.instances.ThingsInstancesRegister;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.DslInfo;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import static cn.huangdayu.things.common.enums.ThingsInstanceType.GATEWAY;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class ThingsServerInstancesDiscoverer extends ThingsBaseInstancesDiscoverer implements ThingsInstancesDiscoverer, ThingsInstancesRegister {

    private final ThingsFrameworkProperties thingsFrameworkProperties;
    private final ThingsEndpointFactory thingsEndpointFactory;
    private final ThingsInstancesDslManager thingsInstancesDslManager;


    public ThingsServerInstancesDiscoverer(ThingsFrameworkProperties thingsFrameworkProperties,
                                           ThingsEndpointFactory thingsEndpointFactory,
                                           ThingsInstancesDslManager thingsInstancesDslManager) {
        super(thingsFrameworkProperties, thingsEndpointFactory, thingsInstancesDslManager);
        this.thingsFrameworkProperties = thingsFrameworkProperties;
        this.thingsEndpointFactory = thingsEndpointFactory;
        this.thingsInstancesDslManager = thingsInstancesDslManager;
    }

    @Override
    public Set<ThingsInstance> allInstance() {
        return getAllThingsInstance(thingsFrameworkProperties.getServers());
    }

    @Override
    public void register(ThingsInstance thingsInstance) {

    }


}
