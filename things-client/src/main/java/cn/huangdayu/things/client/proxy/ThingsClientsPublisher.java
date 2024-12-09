package cn.huangdayu.things.client.proxy;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.api.message.ThingsSender;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static cn.huangdayu.things.common.factory.ThreadPoolFactory.THINGS_EXECUTOR;
import static cn.huangdayu.things.common.utils.ThingsUtils.covertEventMessage;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
public class ThingsClientsPublisher implements ThingsPublisher, ThingsSender {

    private final ThingsEndpointFactory thingsEndpointFactory;

    @Override
    public void publishEvent(ThingsEventMessage tem) {
        publishEvent(covertEventMessage(tem));
    }

    @Override
    public void publishEvent(JsonThingsMessage jtm) {
        THINGS_EXECUTOR.execute(() -> thingsEndpointFactory.create(jtm).handleEvent(jtm));
    }

    @Override
    public JsonThingsMessage sendMessage(JsonThingsMessage jtm) {
        return thingsEndpointFactory.create(jtm).handleMessage(jtm);
    }

    @Override
    public Mono<JsonThingsMessage> sendReactorMessage(JsonThingsMessage jtm) {
        return thingsEndpointFactory.create(jtm, true).reactorMessage(jtm);
    }

}
