package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author huangdayu
 */
public interface ThingsChaining {

    JsonThingsMessage doReceive(JsonThingsMessage jsonThingsMessage);

    void doSubscribe(JsonThingsMessage jsonThingsMessage);

    JsonThingsMessage doSend(JsonThingsMessage jsonThingsMessage);


    void doPublish(ThingsEventMessage thingsEventMessage);

    void doPublish(JsonThingsMessage jsonThingsMessage);

    Mono<JsonThingsMessage> asyncMessage(JsonThingsMessage message);
}
