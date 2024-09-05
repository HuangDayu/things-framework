package cn.huangdayu.things.engine.chaining.sender;

import cn.huangdayu.things.engine.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public interface ThingsSender {

    JsonThingsMessage doSend(JsonThingsMessage message);

}
