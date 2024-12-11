package cn.huangdayu.things.discovery;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.instances.ThingsInstancesDslManager;
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
@RequiredArgsConstructor
public abstract class ThingsBaseInstancesDiscoverer {

    private final ThingsFrameworkProperties thingsFrameworkProperties;
    private final ThingsEndpointFactory thingsEndpointFactory;
    private final ThingsInstancesDslManager thingsInstancesDslManager;


    protected Set<ThingsInstance> getAllThingsInstance(Set<String> servers) {
        Set<ThingsInstance> thingsInstances = new ConcurrentHashSet<>();
        if (CollUtil.isEmpty(servers)) {
            return thingsInstances;
        }
        ThingsInstance thingsInstance = this.thingsFrameworkProperties.getInstance();
        for (String server : servers) {
            try {
                if (server.equals(thingsInstance.getEndpointUri())) {
                    continue;
                }
                DslInfo dsl = thingsEndpointFactory.create(server).getDsl();
                if (dsl != null) {
                    thingsInstances.add(dsl.getInstance());
                    thingsInstancesDslManager.addAllDsl(dsl);
                }
            } catch (Exception e) {
                log.error("Get Things instances to {} server exception : {}", server, e.getMessage());
            }
        }
        return thingsInstances;
    }

    protected ThingsInstance getUpstreamInstance(Set<ThingsInstance> instances) {
        return instances.stream().filter(v -> v.getTypes().contains(GATEWAY)).findFirst().orElseGet(() -> null);
    }
}
