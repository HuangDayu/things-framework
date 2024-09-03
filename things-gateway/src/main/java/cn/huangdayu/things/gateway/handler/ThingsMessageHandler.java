package cn.huangdayu.things.gateway.handler;

import cn.huangdayu.things.engine.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public interface ThingsMessageHandler {

    void handler(JsonThingsMessage jsonThingsMessage);

}
