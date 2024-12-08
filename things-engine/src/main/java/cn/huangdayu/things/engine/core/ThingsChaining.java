package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import reactor.core.publisher.Mono;

/**
 * @author huangdayu
 */
public interface ThingsChaining {

    JsonThingsMessage doReceive(JsonThingsMessage jsonThingsMessage);

    JsonThingsMessage doSend(JsonThingsMessage jsonThingsMessage);

    void doSubscribe(JsonThingsMessage jsonThingsMessage);

    void doPublish(ThingsEventMessage thingsEventMessage);

    void doPublish(JsonThingsMessage jsonThingsMessage);

    Mono<JsonThingsMessage> doReactorReceive(JsonThingsMessage message);


    Mono<JsonThingsMessage> doReactorSend(JsonThingsMessage message);

}
