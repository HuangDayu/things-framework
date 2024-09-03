package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.async.ThingsAsyncManager;
import cn.huangdayu.things.engine.async.ThingsAsyncResponseEvent;
import cn.huangdayu.things.engine.core.ThingsChainingEngine;
import cn.huangdayu.things.engine.core.ThingsObserverEngine;
import cn.huangdayu.things.engine.core.ThingsPublisherEngine;
import cn.huangdayu.things.engine.message.AsyncThingsMessage;
import cn.huangdayu.things.engine.message.JsonThingsMessage;
import cn.huangdayu.things.engine.message.ThingsEventMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static cn.huangdayu.things.engine.async.ThreadPoolFactory.THINGS_EXECUTOR;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
@RequiredArgsConstructor
public class ThingsPublisherExecutor implements ThingsPublisherEngine {
    private final ThingsObserverEngine thingsObserverEngine;
    private final ThingsChainingEngine thingsChainingEngine;

    @PostConstruct
    public void init() {
        thingsObserverEngine.registerObserver(ThingsAsyncResponseEvent.class, engineEvent -> {
            thingsChainingEngine.handler(engineEvent.getJsonThingsMessage());
        });
    }

    @Override
    public void publishEvent(ThingsEventMessage thingsEventMessage) {
        THINGS_EXECUTOR.execute(() -> thingsChainingEngine.handler(thingsEventMessage));
    }

    @Override
    public JsonThingsMessage publishMessage(JsonThingsMessage jsonThingsMessage) {
        return thingsChainingEngine.handler(jsonThingsMessage);
    }

    @Override
    public void publishAsyncMessage(AsyncThingsMessage asyncThingsMessage) {
        THINGS_EXECUTOR.execute(() -> {
            thingsChainingEngine.handler(asyncThingsMessage);
            ThingsAsyncManager.asyncRequest(asyncThingsMessage);
        });
    }

}
