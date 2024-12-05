package cn.huangdayu.things.client.proxy;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.publisher.ThingsPublisher;
import cn.huangdayu.things.common.async.ThingsAsyncManager;
import cn.huangdayu.things.common.event.ThingsAsyncResponseEvent;
import cn.huangdayu.things.common.event.ThingsEventObserver;
import cn.huangdayu.things.common.message.AsyncThingsMessage;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.common.factory.ThreadPoolFactory.THINGS_EXECUTOR;
import static cn.huangdayu.things.common.utils.ThingsUtils.covertEventMessage;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
public class ThingsClientPublisher implements ThingsPublisher {

    private final ThingsEventObserver thingsEventObserver;
    private final ThingsEndpointFactory thingsEndpointFactory;

    @PostConstruct
    public void init() {
        thingsEventObserver.registerObserver(ThingsAsyncResponseEvent.class, engineEvent -> {
            thingsEndpointFactory.create(engineEvent.getJsonThingsMessage()).send(engineEvent.getJsonThingsMessage());
        });
    }

    @Override
    public void publishEvent(ThingsEventMessage thingsEventMessage) {
        JsonThingsMessage jsonThingsMessage = covertEventMessage(thingsEventMessage);
        THINGS_EXECUTOR.execute(() -> thingsEndpointFactory.create(jsonThingsMessage).publish(jsonThingsMessage));
    }

    @Override
    public JsonThingsMessage sendMessage(JsonThingsMessage jsonThingsMessage) {
        return thingsEndpointFactory.create(jsonThingsMessage).send(jsonThingsMessage);
    }

    @Override
    public void sendAsyncMessage(AsyncThingsMessage asyncThingsMessage) {
        THINGS_EXECUTOR.execute(() -> {
            thingsEndpointFactory.create(asyncThingsMessage).send(asyncThingsMessage);
            ThingsAsyncManager.asyncRequest(asyncThingsMessage);
        });
    }

}
