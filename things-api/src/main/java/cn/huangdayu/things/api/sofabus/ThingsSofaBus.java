package cn.huangdayu.things.api.sofabus;

import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.common.enums.ThingsComponentType;
import cn.huangdayu.things.common.properties.ThingsComponentProperties;
import cn.huangdayu.things.common.wrapper.ThingsRequest;

/**
 * @author huangdayu
 */
public interface ThingsSofaBus {

    ThingsComponentType getType();

    void init(ThingsComponentProperties properties);

    boolean start();

    boolean stop();

    boolean output(String topic, ThingsRequest thingsRequest);

    boolean subscribe(String topic, ThingsChaining thingsChaining);

    boolean unsubscribe(String topic);

    boolean isStarted();

}
