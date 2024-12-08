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
public class ThingsClientPublisher implements ThingsPublisher, ThingsSender {

    private final ThingsEndpointFactory thingsEndpointFactory;

    @Override
    public void publishEvent(ThingsEventMessage thingsEventMessage) {
        publishEvent(covertEventMessage(thingsEventMessage));
    }

    @Override
    public void publishEvent(JsonThingsMessage jsonThingsMessage) {
        THINGS_EXECUTOR.execute(() -> thingsEndpointFactory.create(jsonThingsMessage).handleEvent(jsonThingsMessage));
    }

    @Override
    public JsonThingsMessage sendMessage(JsonThingsMessage jsonThingsMessage) {
        return thingsEndpointFactory.create(jsonThingsMessage).handleMessage(jsonThingsMessage);
    }

    @Override
    public Mono<JsonThingsMessage> sendReactorMessage(JsonThingsMessage jsonThingsMessage) {
        return thingsEndpointFactory.create(jsonThingsMessage, true).reactorMessage(jsonThingsMessage);
    }

}
