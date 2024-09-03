package cn.huangdayu.things.engine.exchange.send.restful;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.common.ThingsConstants;
import cn.huangdayu.things.engine.exchange.endpoint.ThingsRestfulEndpoint;
import cn.huangdayu.things.engine.exchange.send.ThingsMessageSender;
import cn.huangdayu.things.engine.message.JsonThingsMessage;
import cn.huangdayu.things.engine.wrapper.ThingsInstance;
import lombok.extern.slf4j.Slf4j;

import static cn.huangdayu.things.engine.exchange.ThingsRestfulClientFactory.createRestClient;

/**
 * @author huangdayu
 */
@ThingsBean
@Slf4j
public class ThingsRestfulMessageSender implements ThingsMessageSender {

    @Override
    public String getProtocol() {
        return ThingsConstants.Protocol.HTTP;
    }

    @Override
    public JsonThingsMessage handler(ThingsInstance thingsInstance, JsonThingsMessage request) {
        return createRestClient(ThingsRestfulEndpoint.class, thingsInstance).handler(request);
    }


}
