package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

/**
 * @author huangdayu
 */
public interface ThingsPublisher {

    /**
     * 发布事件
     * @param tem
     */
    void publishEvent(ThingsEventMessage tem);

    /**
     * 发布事件消息
     * @param jtm
     */
    void publishEvent(JsonThingsMessage jtm);

    /**
     * 同步发送消息
     * @param jtm
     * @return
     */
    JsonThingsMessage syncSendMessage(JsonThingsMessage jtm);

    /**
     * 异步发送消息
     * @param jtm
     * @param consumer
     */
    void asyncSendMessage(JsonThingsMessage jtm, Consumer<JsonThingsMessage> consumer);

    /**
     * 响应式发送消息
     * @param jtm
     * @return
     */
    Mono<JsonThingsMessage> reactorSendMessage(JsonThingsMessage jtm);
}
