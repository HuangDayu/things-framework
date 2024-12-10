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
     * @param jtm
     * @return
     */
    boolean canHandle(JsonThingsMessage jtm);

    /**
     * 同步处理消息
     *
     * @param jtm
     * @return
     */
    JsonThingsMessage syncHandler(JsonThingsMessage jtm);


    /**
     * 异步处理消息
     *
     * @param jtm
     * @return
     */
    Mono<JsonThingsMessage> reactorHandler(JsonThingsMessage jtm);

}
