package cn.huangdayu.things.api.publisher;

import cn.huangdayu.things.common.message.AsyncThingsMessage;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;

public interface ThingsPublisher {
    void publishAsyncMessage(AsyncThingsMessage asyncThingsMessage);

    void publishEvent(ThingsEventMessage thingsEventMessage);

    JsonThingsMessage publishMessage(JsonThingsMessage jsonThingsMessage);
}
