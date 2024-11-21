package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.event.ThingsAsyncResponseEvent;
import cn.huangdayu.things.common.event.ThingsEventObserver;
import cn.huangdayu.things.common.message.AsyncThingsMessage;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import cn.huangdayu.things.engine.async.ThingsAsyncManager;
import cn.huangdayu.things.engine.core.ThingsChainingEngine;
import cn.huangdayu.things.engine.core.ThingsPublisherEngine;
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
public class ThingsPublisherExecutor implements ThingsPublisherEngine {
    private final ThingsEventObserver thingsEventObserver;
    private final ThingsChainingEngine thingsChainingEngine;

    @PostConstruct
    public void init() {
        thingsEventObserver.registerObserver(ThingsAsyncResponseEvent.class, engineEvent -> {
            thingsChainingEngine.send(engineEvent.getJsonThingsMessage());
        });
    }

    @Override
    public void publishEvent(ThingsEventMessage thingsEventMessage) {
        THINGS_EXECUTOR.execute(() -> thingsChainingEngine.publish(thingsEventMessage));
    }

    @Override
    public JsonThingsMessage publishMessage(JsonThingsMessage jsonThingsMessage) {
        return thingsChainingEngine.send(jsonThingsMessage);
    }

    @Override
    public void publishAsyncMessage(AsyncThingsMessage asyncThingsMessage) {
        THINGS_EXECUTOR.execute(() -> {
            thingsChainingEngine.send(asyncThingsMessage);
            ThingsAsyncManager.asyncRequest(asyncThingsMessage);
        });
    }

}
