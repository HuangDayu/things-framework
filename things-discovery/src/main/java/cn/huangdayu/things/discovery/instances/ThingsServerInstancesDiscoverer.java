package cn.huangdayu.things.discovery.instances;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.instances.ThingsInstanceManager;
import cn.huangdayu.things.api.instances.ThingsInstancesDiscoverer;
import cn.huangdayu.things.api.restful.ThingsEndpoint;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
@RequiredArgsConstructor
public class ThingsServerInstancesDiscoverer implements ThingsInstancesDiscoverer {

    private final ThingsFrameworkProperties thingsFrameworkProperties;
    private final ThingsEndpointFactory thingsEndpointFactory;
    private final ThingsInstanceManager thingsInstanceManager;

    @Override
    public Set<ThingsInstance> getAllInstance() {
        return getAllThingsInstance(thingsFrameworkProperties.getServers());
    }

    private Set<ThingsInstance> getAllThingsInstance(Set<String> servers) {
        Set<ThingsInstance> thingsInstances = new ConcurrentHashSet<>();
        if (CollUtil.isEmpty(servers)) {
            return thingsInstances;
        }
        ThingsInstance thingsInstance = this.thingsInstanceManager.getThingsInstance();
        for (String server : servers) {
            try {
                if (server.equals(thingsInstance.getEndpointUri())) {
                    continue;
                }
                thingsInstances.add(thingsEndpointFactory.create(ThingsEndpoint.class, server).exchange(thingsInstance));
            } catch (Exception e) {
                log.error("Get Things instances to {} server exception : {}", server, e.getMessage());
            }
        }
        return thingsInstances;
    }
}
