package cn.huangdayu.things.gateway;

import cn.huangdayu.things.api.endpoint.ThingsEndpoint;
import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dto.ThingsInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static cn.huangdayu.things.common.factory.ThreadPoolFactory.THINGS_EXECUTOR;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsGatewayEndpoint implements ThingsEndpoint {

    private final ThingsInstancesManager thingsInstancesManager;
    private final ThingsEndpointFactory thingsEndpointFactory;
    private final Map<String, ThingsPublisher> thingsPublisherMap;

    @Override
    public Set<ThingsInfo> getThingsDsl() {
        Set<ThingsInfo> thingsDsl = new HashSet<>();
        for (ThingsInstance instance : thingsInstancesManager.getAllThingsInstances()) {
            thingsDsl.addAll(thingsEndpointFactory.create(instance.getEndpointUri()).getThingsDsl());
        }
        return thingsDsl;
    }

    @Override
    public JsonThingsMessage handleMessage(JsonThingsMessage message) {
        return thingsEndpointFactory.create(message).handleMessage(message);
    }

    @Override
    public void handleEvent(JsonThingsMessage message) {
        for (ThingsPublisher thingsPublisher : thingsPublisherMap.values()) {
            THINGS_EXECUTOR.execute(() -> thingsPublisher.publishEvent(message));
        }
    }


    @Override
    public ThingsInstance exchange(ThingsInstance thingsInstance) {
        return thingsInstancesManager.exchangeInstance(thingsInstance);
    }
}
