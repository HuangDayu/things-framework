package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import cn.huangdayu.things.common.message.ThingsResponseMessage;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

/**
 * @author huangdayu
 */
public interface ThingsPublisher {

    /**
     * 发布事件
     *
     * @param tem
     */
    void publishEvent(ThingsEventMessage tem);

    /**
     * 发布事件消息
     *
     * @param trm
     */
    void publishEvent(ThingsRequestMessage trm);

    /**
     * 同步发送消息
     *
     * @param trm
     * @return
     */
    ThingsResponseMessage syncSendMessage(ThingsRequestMessage trm);

    /**
     * 异步发送消息
     *
     * @param trm
     * @param consumer
     */
    void asyncSendMessage(ThingsRequestMessage trm, Consumer<ThingsResponseMessage> consumer);

    /**
     * 响应式发送消息
     *
     * @param trm
     * @return
     */
    Mono<ThingsResponseMessage> reactorSendMessage(ThingsRequestMessage trm);
}
