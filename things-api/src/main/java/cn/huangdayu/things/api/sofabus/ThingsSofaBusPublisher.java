package cn.huangdayu.things.api.sofabus;

import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;

/**
 * @author huangdayu
 */
public interface ThingsSofaBusPublisher {

    void output(ThingsRequest thingsRequest, ThingsResponse thingsResponse);


}
