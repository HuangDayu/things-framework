package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;

/**
 * @author huangdayu
 */
public interface ThingsChainingEngine {

    JsonThingsMessage send(JsonThingsMessage jsonThingsMessage);


    void publish(ThingsEventMessage thingsEventMessage);


}
