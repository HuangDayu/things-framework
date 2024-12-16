package cn.huangdayu.things.api.component;

import cn.huangdayu.things.common.enums.ThingsComponentType;
import cn.huangdayu.things.common.message.BusThingsMessage;
import cn.huangdayu.things.common.properties.ThingsComponentProperties;

import java.util.function.Function;

/**
 * @author huangdayu
 */
public interface ThingsBusComponent {

    ThingsComponentType getType();

    void init(ThingsComponentProperties properties);

    boolean start();

    boolean stop();

    boolean output(String topic, BusThingsMessage ctm);

    boolean subscribe(String topic, Function<BusThingsMessage, BusThingsMessage> btmFunction);

    boolean unsubscribe(String topic);

    boolean isStarted();

}
