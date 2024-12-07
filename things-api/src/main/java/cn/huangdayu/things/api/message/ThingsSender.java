package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.message.AsyncThingsMessage;
import cn.huangdayu.things.common.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public interface ThingsSender {

    /**
     * 发布同步消息
     *
     * @param jsonThingsMessage
     * @return
     */
    JsonThingsMessage sendMessage(JsonThingsMessage jsonThingsMessage);


    /**
     * 发布异步消息
     *
     * @param asyncThingsMessage
     */
    void sendAsyncMessage(AsyncThingsMessage asyncThingsMessage);

}
