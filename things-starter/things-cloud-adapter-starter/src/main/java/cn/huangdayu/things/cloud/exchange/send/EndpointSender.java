package cn.huangdayu.things.cloud.exchange.send;

import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public interface EndpointSender {

    /**
     * 支持的服务类型
     *
     * @return
     * @see ThingsConstants.Protocol
     */
    String endpointProtocol();

    /**
     * 发送消息
     *
     * @param endpointUri
     * @param message
     * @return
     */
    JsonThingsMessage handler(String endpointUri, JsonThingsMessage message);

}
