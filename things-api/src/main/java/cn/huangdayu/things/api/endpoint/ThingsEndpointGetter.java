package cn.huangdayu.things.api.endpoint;

import cn.huangdayu.things.common.enums.EndpointGetterType;
import cn.huangdayu.things.common.message.JsonThingsMessage;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsEndpointGetter {

    EndpointGetterType type();

    /**
     * 订阅发布模式发送消息的端点列表
     * @param thingsMessage
     * @return
     */
    Set<String> getPublishUris(JsonThingsMessage thingsMessage);

    /**
     * 点对点发送消息的端点
     * @param thingsMessage
     * @return
     */
    String getInvokeUri(JsonThingsMessage thingsMessage);
}
