package cn.huangdayu.things.api.receiver;

import cn.huangdayu.things.common.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public interface ThingsReceiver {


    JsonThingsMessage doReceive(JsonThingsMessage message);

    void doSubscribe(JsonThingsMessage message);

}
