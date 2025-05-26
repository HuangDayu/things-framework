package cn.huangdayu.things.api.sofabus;

import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.common.wrapper.ThingsSubscribes;

/**
 * @author huangdayu
 */
public interface ThingsSofaBus {

    ThingsSofaBusType getType();

    boolean start();

    boolean stop();

    boolean output(ThingsRequest thingsRequest, ThingsResponse thingsResponse);

    boolean subscribe(ThingsSubscribes thingsSubscribes);

    boolean unsubscribe(ThingsSubscribes thingsSubscribes);

    boolean isStarted();

}
