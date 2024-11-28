package cn.huangdayu.things.api.restful;


import cn.huangdayu.things.common.dto.ThingsInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.Set;

/**
 * 消息点对点发布
 *
 * @author huangdayu
 */
@HttpExchange
public interface ThingsEndpoint {

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
    @PostExchange("/things/send")
    JsonThingsMessage send(@RequestBody JsonThingsMessage message);

    /**
     * 发布消息
     * @param message
     */
    @PostExchange("/things/publish")
    void publish(@RequestBody JsonThingsMessage message);

    /**
     * 交换实例信息
     *
     * @param thingsInstance 请求者的实例信息
     * @return 本实例的信息
     */
    @PostExchange("/things/exchange")
    ThingsInstance exchange(@RequestBody ThingsInstance thingsInstance);


}
