package cn.huangdayu.things.discovery.instances;

import cn.huangdayu.things.api.instances.ThingsInstances;
import cn.huangdayu.things.api.restful.ThingsEndpoint;
import cn.huangdayu.things.common.factory.RestfulClientFactory;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;


/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@Slf4j
public abstract class ThingsRestfulInstancesGetter {

    protected final ThingsInstances thingsInstances;


    protected Set<ThingsInstance> getAllThingsInstance(Set<String> servers) {
        Set<ThingsInstance> thingsInstances = new ConcurrentHashSet<>();
        if (CollUtil.isEmpty(servers)) {
            return thingsInstances;
        }
        ThingsInstance thingsInstance = this.thingsInstances.getThingsInstance();
        for (String server : servers) {
            try {
                if (server.equals(thingsInstance.getEndpointUri())) {
                    continue;
                }
                thingsInstances.add(RestfulClientFactory.createRestClient(ThingsEndpoint.class, server).exchange(thingsInstance));
            } catch (Exception e) {
                log.error("Get Things instances to {} server exception : {}", server, e.getMessage());
            }
        }
        return thingsInstances;
    }

}
