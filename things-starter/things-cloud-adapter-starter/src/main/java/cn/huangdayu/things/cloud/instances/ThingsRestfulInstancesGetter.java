package cn.huangdayu.things.cloud.instances;

import cn.huangdayu.things.engine.core.ThingsInstancesEngine;
import cn.huangdayu.things.engine.endpoint.ThingsRestfulEndpoint;
import cn.huangdayu.things.engine.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import static cn.huangdayu.things.cloud.exchange.ThingsRestfulClientFactory.createRestClient;


/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@Slf4j
public abstract class ThingsRestfulInstancesGetter {

    protected final ThingsInstancesEngine thingsInstancesEngine;


    protected Set<ThingsInstance> getAllThingsInstance(Set<String> servers) {
        Set<ThingsInstance> thingsInstances = new ConcurrentHashSet<>();
        if (CollUtil.isEmpty(servers)) {
            return thingsInstances;
        }
        ThingsInstance thingsInstance = thingsInstancesEngine.getThingsInstance();
        for (String server : servers) {
            try {
                if (server.equals(thingsInstance.getEndpointUri())) {
                    continue;
                }
                thingsInstances.add(createRestClient(ThingsRestfulEndpoint.class, server).exchangeInstance(thingsInstance));
            } catch (Exception e) {
                log.error("Get Things instances to {} server exception : {}", server, e.getMessage());
            }
        }
        return thingsInstances;
    }

}
