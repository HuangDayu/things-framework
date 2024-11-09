package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.common.message.AsyncThingsMessage;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;

/**
 * @author huangdayu
 */
public interface ThingsPublisherEngine {

    /**
     * 发布事件
     *
     * @param thingsEventMessage
     */
    void publishEvent(ThingsEventMessage thingsEventMessage);


    /**
     * 发布同步消息
     *
     * @param jsonThingsMessage
     * @return
     */
    JsonThingsMessage publishMessage(JsonThingsMessage jsonThingsMessage);


    /**
     * 发布异步消息
     *
     * @param asyncThingsMessage
     */
    void publishAsyncMessage(AsyncThingsMessage asyncThingsMessage);

}
