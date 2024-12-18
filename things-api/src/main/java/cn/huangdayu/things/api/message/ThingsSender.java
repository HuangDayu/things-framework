package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import reactor.core.publisher.Mono;

/**
 * @author huangdayu
 */
public interface ThingsSender {

    JsonThingsMessage sendMessage(JsonThingsMessage jtm);

    Mono<JsonThingsMessage> sendReactorMessage(JsonThingsMessage jtm);

}
