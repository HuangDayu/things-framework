package cn.huangdayu.things.client.exchange;

import cn.huangdayu.things.api.endpoint.ThingsEndpointSender;
import cn.huangdayu.things.api.restful.ThingsEndpoint;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.factory.RestfulClientFactory;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangdayu
 */
@ThingsBean
@Slf4j
public class RestEndpointSender implements ThingsEndpointSender {

    @Override
    public String endpointProtocol() {
        return ThingsConstants.Protocol.REST;
    }

    @Override
    public JsonThingsMessage handler(String endpointUri, JsonThingsMessage message) {
        return RestfulClientFactory.createRestClient(ThingsEndpoint.class, endpointUri).send(message);
    }


}
