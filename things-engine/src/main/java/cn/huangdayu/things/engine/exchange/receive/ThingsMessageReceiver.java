package cn.huangdayu.things.engine.exchange.receive;

import cn.huangdayu.things.engine.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public interface ThingsMessageReceiver {

    /**
     * 处理消息
     *
     * @param message
     * @return
     */
    JsonThingsMessage handler(JsonThingsMessage message);

}
