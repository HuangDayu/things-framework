package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

/**
 * @author huangdayu
 */
public interface ThingsSender {

    /**
     * 发布同步消息
     *
     * @param jsonThingsMessage
     * @return
     */
    JsonThingsMessage sendMessage(JsonThingsMessage jsonThingsMessage);


    /**
     * 发布异步消息
     *
     * @param jsonThingsMessage
     */
    Mono<JsonThingsMessage> sendAsyncMessage(JsonThingsMessage jsonThingsMessage);

}
