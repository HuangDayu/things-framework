package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.api.message.ThingsHandling;
import cn.huangdayu.things.common.annotation.ThingsHandler;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.common.enums.ThingsStreamingType.OUTPUTTING;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsHandler(order = 1, source = OUTPUTTING)
public class ThingsSofaBusHandling implements ThingsHandling {

    private final ThingsSofaBusFactory thingsSofaBusFactory;
    private final ThingsSofaBusTopicManager thingsSofaBusTopicManager;

    @Override
    public void doHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {

    }
}
