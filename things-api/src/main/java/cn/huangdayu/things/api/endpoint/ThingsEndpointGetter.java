package cn.huangdayu.things.api.endpoint;

import cn.huangdayu.things.common.enums.EndpointGetterType;
import cn.huangdayu.things.common.message.JsonThingsMessage;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsEndpointGetter {

    EndpointGetterType type();

    Set<String> getPublishUris(JsonThingsMessage thingsMessage);

    String getInvokeUri(JsonThingsMessage thingsMessage);
}
