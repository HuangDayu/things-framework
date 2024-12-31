package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.api.message.ThingsHandling;
import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.common.annotation.ThingsHandler;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static cn.huangdayu.things.common.enums.ThingsStreamingType.OUTPUTTING;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsHandler(order = 1, source = OUTPUTTING)
public class ThingsSofaBusHandling implements ThingsHandling {

    private final ThingsSofaBusFactory thingsSofaBusFactory;
    private final ThingsSofaBusTopics thingsSofaBusTopics;

    @Override
    public void doHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        Set<ThingsSofaBus> thingsSofaBus = thingsSofaBusFactory.getAllSofaBus();
        for (ThingsSofaBus bus : thingsSofaBus) {
            if (bus.isStarted()) {
                for (String topicCode : thingsSofaBusTopics.getSubscribeTopics(thingsRequest)) {
                    bus.output(topicCode, thingsRequest, thingsResponse);
                }
            }
        }
    }
}
