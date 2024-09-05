package cn.huangdayu.things.engine.chaining.receiver;

import cn.huangdayu.things.engine.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public interface ThingsReceiver {


    JsonThingsMessage doReceive(JsonThingsMessage message);

}
