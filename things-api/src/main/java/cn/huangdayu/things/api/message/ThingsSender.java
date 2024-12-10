package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import reactor.core.publisher.Mono;

/**
 * @author huangdayu
 */
public interface ThingsSender {

    /**
     * 发布同步消息
     *
     * @param jtm
     * @return
     */
    JsonThingsMessage sendMessage(JsonThingsMessage jtm);


    /**
     * 发布异步消息
     *
     * @param jtm
     */
    Mono<JsonThingsMessage> sendReactorMessage(JsonThingsMessage jtm);

}
