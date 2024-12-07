package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.api.message.ThingsSender;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import cn.huangdayu.things.engine.core.ThingsChaining;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

import static cn.huangdayu.things.common.factory.ThreadPoolFactory.THINGS_EXECUTOR;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
@RequiredArgsConstructor
public class ThingsPublisherExecutor implements ThingsPublisher, ThingsSender {
    private final ThingsChaining thingsChaining;


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
    public CompletableFuture<JsonThingsMessage> sendAsyncMessage(JsonThingsMessage jsonThingsMessage) {
        return thingsChaining.asyncMessage(jsonThingsMessage);
    }

}
