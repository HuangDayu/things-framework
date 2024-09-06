package cn.huangdayu.things.engine.chaining.sender;

import cn.huangdayu.things.engine.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public interface ThingsSender {


    /**
     * 能否发送该消息
     *
     * @param message
     * @return
     */
    boolean canSend(JsonThingsMessage message);

    /**
     * 点对点发送（服务/属性）
     *
     * @param message
     * @return
     */
    JsonThingsMessage doSend(JsonThingsMessage message);


    /**
     * 发布消息（事件）
     *
     * @param message
     */
    void doPublish(JsonThingsMessage message);
}
