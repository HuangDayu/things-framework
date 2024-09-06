package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.engine.message.JsonThingsMessage;
import cn.huangdayu.things.engine.message.ThingsEventMessage;

/**
 * @author huangdayu
 */
public interface ThingsChainingEngine {

    JsonThingsMessage send(JsonThingsMessage jsonThingsMessage);


    void publish(ThingsEventMessage thingsEventMessage);


}
