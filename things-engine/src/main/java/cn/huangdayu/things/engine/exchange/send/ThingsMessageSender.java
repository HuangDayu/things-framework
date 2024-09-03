package cn.huangdayu.things.engine.exchange.send;

import cn.huangdayu.things.engine.message.JsonThingsMessage;
import cn.huangdayu.things.engine.wrapper.ThingsInstance;

/**
 * @author huangdayu
 */
public interface ThingsMessageSender {

    /**
     * 支持的服务类型
     *
     * @return
     * @see cn.huangdayu.things.engine.common.ThingsConstants.Protocol
     */
    String getProtocol();

    /**
     * 发送消息
     *
     * @param thingsInstance
     * @param message
     * @return
     */
    JsonThingsMessage handler(ThingsInstance thingsInstance, JsonThingsMessage message);

}
