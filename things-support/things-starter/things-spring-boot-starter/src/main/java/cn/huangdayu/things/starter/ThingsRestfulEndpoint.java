package cn.huangdayu.things.starter;


import cn.huangdayu.things.starter.endpoint.ThingsEndpoint;
import cn.huangdayu.things.common.dsl.DslInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

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
    DslInfo getDsl();

    /**
     * 点对点发送消息
     *
     * @param jtm
     * @return
     */
    @PostExchange("/things/message")
    JsonThingsMessage handleMessage(@RequestBody JsonThingsMessage jtm);


    /**
     * 异步消息发送
     *
     * @param jtm
     * @return
     */
    @PostExchange("/things/message/reactor")
    Mono<JsonThingsMessage> reactorMessage(@RequestBody JsonThingsMessage jtm);

    /**
     * 发布消息
     *
     * @param jtm
     */
    @PostExchange("/things/event")
    void handleEvent(@RequestBody JsonThingsMessage jtm);


    /**
     * 配置实例
     */
    @PostExchange("/things/configuration")
    void configuration(@RequestBody ThingsConfiguration thingsConfiguration);
}
