package cn.huangdayu.things.client.exchange;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.endpoint.ThingsEndpointSender;
import cn.huangdayu.things.api.restful.ThingsEndpoint;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.enums.EndpointProtocolType;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static cn.huangdayu.things.common.enums.EndpointProtocolType.REST;

/**
 * @author huangdayu
 */
@ThingsBean
@Slf4j
@RequiredArgsConstructor
public class RestEndpointSender implements ThingsEndpointSender {

    private final ThingsEndpointFactory thingsEndpointFactory;

    @Override
    public EndpointProtocolType endpointProtocol() {
        return REST;
    }

    @Override
    public JsonThingsMessage handler(String endpointUri, JsonThingsMessage message) {
        return thingsEndpointFactory.create(ThingsEndpoint.class, endpointUri).send(message);
    }


}
