package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import reactor.core.publisher.Mono;

/**
 * @author huangdayu
 */
public interface ThingsChaining {

    JsonThingsMessage doReceive(JsonThingsMessage jtm);

    JsonThingsMessage doSend(JsonThingsMessage jtm);

    void doSubscribe(JsonThingsMessage jtm);

    void doPublish(ThingsEventMessage tem);

    void doPublish(JsonThingsMessage jtm);

    Mono<JsonThingsMessage> doReactorReceive(JsonThingsMessage jtm);


    Mono<JsonThingsMessage> doReactorSend(JsonThingsMessage jtm);

}
