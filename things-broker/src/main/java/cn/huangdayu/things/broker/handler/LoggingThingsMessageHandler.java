package cn.huangdayu.things.broker.handler;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
@ThingsBean
public class LoggingThingsMessageHandler implements ThingsMessageHandler{
    @Override
    public void handler(JsonThingsMessage jsonThingsMessage) {

    }
}