package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.engine.message.JsonThingsMessage;
import cn.huangdayu.things.engine.message.ThingsEventMessage;

/**
 * @author huangdayu
 */
public interface ThingsChainingEngine {

    JsonThingsMessage handler(JsonThingsMessage jsonThingsMessage);


    void handler(ThingsEventMessage thingsEventMessage);


}
