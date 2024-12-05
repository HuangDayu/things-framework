package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;

/**
 * @author huangdayu
 */
public interface ThingsChaining {

    JsonThingsMessage doReceive(JsonThingsMessage jsonThingsMessage);

    void doSubscribe(JsonThingsMessage jsonThingsMessage);

    JsonThingsMessage doSend(JsonThingsMessage jsonThingsMessage);


    void doPublish(ThingsEventMessage thingsEventMessage);


}
