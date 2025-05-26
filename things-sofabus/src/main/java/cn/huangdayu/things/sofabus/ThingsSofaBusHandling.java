package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.api.message.ThingsHandling;
import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.common.annotation.ThingsHandler;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static cn.huangdayu.things.common.enums.ThingsChainingType.OUTPUTTING;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsHandler(order = 1, chainingType = OUTPUTTING)
public class ThingsSofaBusHandling implements ThingsHandling {

    private final ThingsSofaBusManager thingsSofaBusManager;

    @Override
    public boolean canHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        return CollUtil.isNotEmpty(thingsSofaBusManager.getAllSofaBus());
    }

    @Override
    public void doHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        Set<ThingsSofaBus> thingsSofaBus = thingsSofaBusManager.getAllSofaBus();
        for (ThingsSofaBus bus : thingsSofaBus) {
            if (bus.isStarted()) {
                bus.output(thingsRequest, thingsResponse);
            }
        }
    }
}
