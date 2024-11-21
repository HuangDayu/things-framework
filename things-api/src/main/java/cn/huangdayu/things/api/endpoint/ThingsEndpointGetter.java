package cn.huangdayu.things.api.endpoint;

import cn.huangdayu.things.common.message.JsonThingsMessage;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsEndpointGetter {


    Set<String> getTargetEndpointUris(JsonThingsMessage thingsMessage);

    String getTargetEndpointUri(JsonThingsMessage thingsMessage);
}
