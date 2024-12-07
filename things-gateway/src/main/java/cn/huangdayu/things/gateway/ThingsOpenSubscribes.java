package cn.huangdayu.things.gateway;

import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import lombok.extern.slf4j.Slf4j;

import static cn.huangdayu.things.common.utils.ThingsUtils.covertEventMessage;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class ThingsOpenSubscribes implements ThingsPublisher {
    @Override
    public void publishEvent(ThingsEventMessage thingsEventMessage) {
        publishEvent(covertEventMessage(thingsEventMessage));
    }

    @Override
    public void publishEvent(JsonThingsMessage jsonThingsMessage) {
        log.debug("Things send message to open subscribes : {}", jsonThingsMessage);
    }
}
