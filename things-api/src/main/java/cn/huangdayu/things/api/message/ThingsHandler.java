package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import reactor.core.publisher.Mono;

/**
 * @author huangdayu
 */
public interface ThingsHandler {

    /**
     * 是否能处理这条消息
     *
     * @param jsonThingsMessage
     * @return
     */
    boolean canHandle(JsonThingsMessage jsonThingsMessage);

    /**
     * 同步处理消息
     *
     * @param jsonThingsMessage
     * @return
     */
    JsonThingsMessage syncHandler(JsonThingsMessage jsonThingsMessage);


    /**
     * 异步处理消息
     *
     * @param jsonThingsMessage
     * @return
     */
    Mono<JsonThingsMessage> reactorHandler(JsonThingsMessage jsonThingsMessage);

}
