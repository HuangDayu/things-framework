package cn.huangdayu.things.client.exchange;

import cn.huangdayu.things.api.endpoint.ThingsEndpointGetter;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.EndpointGetterType;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsProperties;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class DefaultEndpointGetter implements ThingsEndpointGetter {

    private final ThingsProperties thingsProperties;

    @Override
    public EndpointGetterType type() {
        return EndpointGetterType.DEFAULT;
    }

    @Override
    public Set<String> getPublishUris(JsonThingsMessage thingsMessage) {
        return Set.of(thingsProperties.getGateway().getUri());
    }

    @Override
    public String getSendUri(JsonThingsMessage thingsMessage) {
        return thingsProperties.getGateway().getUri();
    }
}
