package cn.huangdayu.things.gateway;

import cn.huangdayu.things.api.endpoint.ThingsEndpoint;
import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.instances.ThingsInstancesDslManager;
import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.DslInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsConfiguration;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Map;

import static cn.huangdayu.things.common.factory.ThreadPoolFactory.THINGS_EXECUTOR;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsGatewayEndpoint implements ThingsEndpoint {

    private final ThingsInstancesDslManager thingsInstancesManager;
    private final ThingsEndpointFactory thingsEndpointFactory;
    private final Map<String, ThingsPublisher> thingsPublisherMap;

    @Override
    public DslInfo getDsl() {
        return thingsInstancesManager.getDsl();
    }

    @Override
    public JsonThingsMessage handleMessage(JsonThingsMessage jtm) {
        return thingsEndpointFactory.create(jtm).handleMessage(jtm);
    }

    @Override
    public Mono<JsonThingsMessage> reactorMessage(JsonThingsMessage jtm) {
        return thingsEndpointFactory.create(jtm, true).reactorMessage(jtm);
    }

    @Override
    public void handleEvent(JsonThingsMessage jtm) {
        for (ThingsPublisher thingsPublisher : thingsPublisherMap.values()) {
            THINGS_EXECUTOR.execute(() -> thingsPublisher.publishEvent(jtm));
        }
    }

    @Override
    public void configuration(ThingsConfiguration thingsConfiguration) {

    }


}
