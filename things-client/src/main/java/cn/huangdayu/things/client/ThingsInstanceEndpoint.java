package cn.huangdayu.things.client;


import cn.huangdayu.things.common.message.JsonThingsMessage;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * 消息点对点发布
 *
 * @author huangdayu
 */
@HttpExchange
public interface ThingsInstanceEndpoint {


    /**
     * 发送消息
     *
     * @param message
     * @return
     */
    @PostExchange("/things/message")
    JsonThingsMessage handler(@RequestBody JsonThingsMessage message);


}
