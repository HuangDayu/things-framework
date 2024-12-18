package cn.huangdayu.things.starter.endpoint;

import cn.huangdayu.things.common.message.JsonThingsMessage;

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
     * 点对点发送消息的端点
     *
     * @param jtm
     * @return
     */
    String getEndpointUri(JsonThingsMessage jtm);
}
