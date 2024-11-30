package cn.huangdayu.things.client.exchange;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.endpoint.ThingsEndpointSender;
import cn.huangdayu.things.api.restful.ThingsEndpoint;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangdayu
 */
@ThingsBean
@Slf4j
@RequiredArgsConstructor
public class RestEndpointSender implements ThingsEndpointSender {

    private final ThingsEndpointFactory thingsEndpointFactory;

    @Override
    public String endpointProtocol() {
        return ThingsConstants.Protocol.REST;
    }

    @Override
    public JsonThingsMessage handler(String endpointUri, JsonThingsMessage message) {
        return thingsEndpointFactory.create(ThingsEndpoint.class, endpointUri).send(message);
    }


}
