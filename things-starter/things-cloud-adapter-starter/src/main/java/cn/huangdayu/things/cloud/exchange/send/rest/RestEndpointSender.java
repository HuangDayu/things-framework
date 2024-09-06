package cn.huangdayu.things.cloud.exchange.send.rest;

import cn.huangdayu.things.cloud.exchange.send.EndpointSender;
import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.common.ThingsConstants;
import cn.huangdayu.things.engine.endpoint.ThingsRestfulEndpoint;
import cn.huangdayu.things.engine.message.JsonThingsMessage;
import cn.huangdayu.things.engine.wrapper.ThingsInstance;
import lombok.extern.slf4j.Slf4j;

import static cn.huangdayu.things.cloud.exchange.ThingsRestfulClientFactory.createRestClient;

/**
 * @author huangdayu
 */
@ThingsBean
@Slf4j
public class RestEndpointSender implements EndpointSender {

    @Override
    public String endpointProtocol() {
        return ThingsConstants.Protocol.REST;
    }

    @Override
    public JsonThingsMessage handler(String endpointUri, JsonThingsMessage message) {
        return createRestClient(ThingsRestfulEndpoint.class, endpointUri).handler(message);
    }


}
