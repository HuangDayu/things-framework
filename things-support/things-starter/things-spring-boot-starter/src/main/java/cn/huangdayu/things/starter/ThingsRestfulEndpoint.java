package cn.huangdayu.things.starter;


import cn.huangdayu.things.api.endpoint.ThingsEndpoint;
import cn.huangdayu.things.common.dto.ThingsInfo;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 消息点对点发布
 *
 * @author huangdayu
 */
@HttpExchange
public interface ThingsRestfulEndpoint extends ThingsEndpoint {

    /**
     * 获取物模型支持列表
     *
     * @return
     */
    @GetExchange("/things/dsl")
    Set<ThingsInfo> getThingsDsl();

    /**
     * 点对点发送消息
     *
     * @param message
     * @return
     */
    @PostExchange("/things/message")
    JsonThingsMessage handleMessage(@RequestBody JsonThingsMessage message);


    /**
     * 异步消息发送
     *
     * @param message
     * @return
     */
    @PostExchange("/things/message/reactor")
    Mono<JsonThingsMessage> reactorMessage(@RequestBody JsonThingsMessage message);

    /**
     * 发布消息
     *
     * @param message
     */
    @PostExchange("/things/event")
    void handleEvent(@RequestBody JsonThingsMessage message);

    /**
     * 交换实例信息
     *
     * @param thingsInstance 请求者的实例信息
     * @return 本实例的信息
     */
    @PostExchange("/things/exchange")
    ThingsInstance exchange(@RequestBody ThingsInstance thingsInstance);


}
