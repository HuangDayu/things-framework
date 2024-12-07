package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.api.message.ThingsSender;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.async.ThingsAsyncManager;
import cn.huangdayu.things.common.event.ThingsAsyncResponseEvent;
import cn.huangdayu.things.common.event.ThingsEventObserver;
import cn.huangdayu.things.common.message.AsyncThingsMessage;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import cn.huangdayu.things.engine.core.ThingsChaining;
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
public class ThingsPublisherExecutor implements ThingsPublisher, ThingsSender {
    private final ThingsEventObserver thingsEventObserver;
    private final ThingsChaining thingsChaining;

    @PostConstruct
    public void init() {
        thingsEventObserver.registerObserver(ThingsAsyncResponseEvent.class, engineEvent -> {
            thingsChaining.doSend(engineEvent.getJsonThingsMessage());
        });
    }

    @Override
    public void publishEvent(ThingsEventMessage thingsEventMessage) {
        THINGS_EXECUTOR.execute(() -> thingsChaining.doPublish(thingsEventMessage));
    }

    @Override
    public void publishEvent(JsonThingsMessage jsonThingsMessage) {
        THINGS_EXECUTOR.execute(() -> thingsChaining.doPublish(jsonThingsMessage));
    }

    @Override
    public JsonThingsMessage sendMessage(JsonThingsMessage jsonThingsMessage) {
        return thingsChaining.doSend(jsonThingsMessage);
    }

    @Override
    public void sendAsyncMessage(AsyncThingsMessage asyncThingsMessage) {
        THINGS_EXECUTOR.execute(() -> {
            thingsChaining.doSend(asyncThingsMessage);
            ThingsAsyncManager.asyncRequest(asyncThingsMessage);
        });
    }

}
