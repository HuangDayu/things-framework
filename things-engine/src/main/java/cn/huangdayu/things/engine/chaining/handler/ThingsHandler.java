package cn.huangdayu.things.engine.chaining.handler;

import cn.huangdayu.things.engine.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public interface ThingsHandler {


    JsonThingsMessage doHandler(JsonThingsMessage jsonThingsMessage);

}
