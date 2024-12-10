package cn.huangdayu.things.gateway;

import cn.huangdayu.things.api.endpoint.ThingsEndpoint;
import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.DomainInfo;
import cn.huangdayu.things.common.dsl.DslInfo;
import cn.huangdayu.things.common.dsl.ThingsInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

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
    public DslInfo getDsl() {
        Set<ThingsInfo> thingsDsl = new HashSet<>();
        Set<DomainInfo> domainDsl = new HashSet<>();
        for (ThingsInstance instance : thingsInstancesManager.getAllThingsInstances()) {
            DslInfo dsl = thingsEndpointFactory.create(instance.getEndpointUri()).getDsl();
            if (CollUtil.isNotEmpty(dsl.getThingsDsl())) {
                thingsDsl.addAll(dsl.getThingsDsl());
            }
            if (CollUtil.isNotEmpty(dsl.getDomainDsl())) {
                domainDsl.addAll(dsl.getDomainDsl());
            }
        }
        return new DslInfo(domainDsl, thingsDsl);
    }

    @Override
    public JsonThingsMessage handleMessage(JsonThingsMessage message) {
        return thingsEndpointFactory.create(message).handleMessage(message);
    }

    @Override
    public Mono<JsonThingsMessage> reactorMessage(JsonThingsMessage message) {
        return thingsEndpointFactory.create(message, true).reactorMessage(message);
    }

    @Override
    public void handleEvent(JsonThingsMessage message) {
        for (ThingsPublisher thingsPublisher : thingsPublisherMap.values()) {
            THINGS_EXECUTOR.execute(() -> thingsPublisher.publishEvent(message));
        }
    }


    @Override
    public ThingsInstance exchangeInstance(ThingsInstance thingsInstance) {
        return thingsInstancesManager.exchangeInstance(thingsInstance);
    }
}
