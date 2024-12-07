package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;

/**
 * @author huangdayu
 */
public interface ThingsPublisher {

    /**
     * 发布事件
     *
     * @param thingsEventMessage
     */
    void publishEvent(ThingsEventMessage thingsEventMessage);


    /**
     * 发布事件
     *
     * @param jsonThingsMessage
     */
    void publishEvent(JsonThingsMessage jsonThingsMessage);
}
