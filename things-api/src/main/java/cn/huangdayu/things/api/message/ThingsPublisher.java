package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;

/**
 * @author huangdayu
 */
public interface ThingsPublisher {

    /**
     * 发布事件
     *
     * @param tem
     */
    void publishEvent(ThingsEventMessage tem);


    /**
     * 发布事件
     *
     * @param jtm
     */
    void publishEvent(JsonThingsMessage jtm);
}
