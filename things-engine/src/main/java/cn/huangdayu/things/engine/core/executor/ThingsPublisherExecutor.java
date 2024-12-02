package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.event.ThingsAsyncResponseEvent;
import cn.huangdayu.things.common.event.ThingsEventObserver;
import cn.huangdayu.things.common.message.AsyncThingsMessage;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import cn.huangdayu.things.engine.async.ThingsAsyncManager;
import cn.huangdayu.things.engine.core.ThingsChaining;
import cn.huangdayu.things.api.publisher.ThingsPublisher;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static cn.huangdayu.things.common.factory.ThreadPoolFactory.THINGS_EXECUTOR;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
@RequiredArgsConstructor
public class ThingsPublisherExecutor implements ThingsPublisher {
    private final ThingsEventObserver thingsEventObserver;
    private final ThingsChaining thingsChaining;

    @PostConstruct
    public void init() {
        thingsEventObserver.registerObserver(ThingsAsyncResponseEvent.class, engineEvent -> {
            thingsChaining.send(engineEvent.getJsonThingsMessage());
        });
    }

    @Override
    public void publishEvent(ThingsEventMessage thingsEventMessage) {
        THINGS_EXECUTOR.execute(() -> thingsChaining.publish(thingsEventMessage));
    }

    @Override
    public JsonThingsMessage publishMessage(JsonThingsMessage jsonThingsMessage) {
        return thingsChaining.send(jsonThingsMessage);
    }

    @Override
    public void publishAsyncMessage(AsyncThingsMessage asyncThingsMessage) {
        THINGS_EXECUTOR.execute(() -> {
            thingsChaining.send(asyncThingsMessage);
            ThingsAsyncManager.asyncRequest(asyncThingsMessage);
        });
    }

}
