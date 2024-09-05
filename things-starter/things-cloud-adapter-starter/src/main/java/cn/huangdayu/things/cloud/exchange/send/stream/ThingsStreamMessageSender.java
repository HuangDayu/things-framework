package cn.huangdayu.things.cloud.exchange.send.stream;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.common.ThingsConstants;
import cn.huangdayu.things.cloud.exchange.send.ThingsMessageSender;
import cn.huangdayu.things.engine.message.JsonThingsMessage;
import cn.huangdayu.things.engine.wrapper.ThingsInstance;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsStreamMessageSender implements ThingsMessageSender {

    @Override
    public String getProtocol() {
        return ThingsConstants.Protocol.AMQP;
    }

    @Override
    public JsonThingsMessage handler(ThingsInstance thingsInstance, JsonThingsMessage message) {
        return null;
    }
}
