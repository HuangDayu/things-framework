package cn.huangdayu.things.rest.endpoint;


import cn.huangdayu.things.common.dsl.DslInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsConfiguration;
import reactor.core.publisher.Mono;

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
    DslInfo getDsl();

    /**
     * 点对点发送消息
     *
     * @param jtm
     * @return
     */
    JsonThingsMessage handleMessage(JsonThingsMessage jtm);


    /**
     * 异步消息发送
     *
     * @param jtm
     * @return
     */
    Mono<JsonThingsMessage> reactorMessage(JsonThingsMessage jtm);

    /**
     * 发布消息
     *
     * @param jtm
     */
    void handleEvent(JsonThingsMessage jtm);


    /**
     * 配置实例
     * 由管理服务进行配置下发
     */
    void configuration(ThingsConfiguration thingsConfiguration);


}
