package cn.huangdayu.things.api.endpoint;

import cn.huangdayu.things.common.enums.EndpointGetterType;
import cn.huangdayu.things.common.message.JsonThingsMessage;

import java.util.Set;

/**
 * 端点获取器，解决发送给谁的问题
 *
 * @author huangdayu
 */
public interface ThingsEndpointGetter {

    /**
     * 获取端点获取方式
     *
     * @return
     */
    EndpointGetterType type();

    /**
     * 订阅发布模式发送消息的端点列表
     *
     * @param thingsMessage
     * @return
     */
    Set<String> getPublishUris(JsonThingsMessage thingsMessage);

    /**
     * 点对点发送消息的端点
     *
     * @param thingsMessage
     * @return
     */
    String getSendUri(JsonThingsMessage thingsMessage);
}
