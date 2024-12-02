package cn.huangdayu.things.api.endpoint;

import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.enums.EndpointProtocolType;
import cn.huangdayu.things.common.message.JsonThingsMessage;

/**
 * 端点发送器，解决怎么发送的问题
 *
 * @author huangdayu
 */
public interface ThingsEndpointSender {

    /**
     * 支持的服务类型
     *
     * @return
     * @see ThingsConstants.Protocol
     */
    EndpointProtocolType endpointProtocol();

    /**
     * 发送消息
     *
     * @param endpointUri
     * @param message
     * @return
     */
    JsonThingsMessage handler(String endpointUri, JsonThingsMessage message);

}
