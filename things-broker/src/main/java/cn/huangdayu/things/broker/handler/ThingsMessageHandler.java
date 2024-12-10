package cn.huangdayu.things.broker.handler;

import cn.huangdayu.things.common.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public interface ThingsMessageHandler {

    void handler(JsonThingsMessage jtm);

}
