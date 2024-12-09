package cn.huangdayu.things.api.endpoint;


import cn.huangdayu.things.common.annotation.ThingsExchange;
import cn.huangdayu.things.common.dsl.DslInfo;
import cn.huangdayu.things.common.dsl.ThingsInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * 消息点对点发布
 *
 * @author huangdayu
 */
public interface ThingsEndpoint {

    /**
     * 获取物模型支持列表
     *
     * @return
     */
    @ThingsExchange(identifier = "get-dsl")
    DslInfo getDsl();

    /**
     * 点对点发送消息
     *
     * @param jtm
     * @return
     */
    @ThingsExchange(identifier = "handle-message")
    JsonThingsMessage handleMessage(JsonThingsMessage jtm);


    /**
     * 异步消息发送
     *
     * @param jtm
     * @return
     */
    @ThingsExchange(identifier = "reactor-message")
    Mono<JsonThingsMessage> reactorMessage(JsonThingsMessage jtm);

    /**
     * 发布消息
     *
     * @param jtm
     */
    @ThingsExchange(identifier = "handle-event")
    void handleEvent(JsonThingsMessage jtm);

    /**
     * 交换实例信息
     *
     * @param thingsInstance 请求者的实例信息
     * @return 本实例的信息
     */
    @ThingsExchange(identifier = "exchange-instance")
    ThingsInstance exchangeInstance(ThingsInstance thingsInstance);


}
