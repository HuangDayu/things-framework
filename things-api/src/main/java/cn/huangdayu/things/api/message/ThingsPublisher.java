package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;

/**
 * @author huangdayu
 */
public interface ThingsPublisher {

    void publishEvent(ThingsEventMessage tem);

    void publishEvent(JsonThingsMessage jtm);

}
