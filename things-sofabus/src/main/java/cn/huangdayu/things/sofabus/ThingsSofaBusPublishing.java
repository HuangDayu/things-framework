package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusPublisher;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsSofaBusPublishing implements ThingsSofaBusPublisher {

    private final ThingsSofaBusManager thingsSofaBusManager;

    /**
     * 输出消息，如果指定目标SofaBus则指定输出，否则全部多sofaBus都输出
     *
     * @param thingsRequest
     * @param thingsResponse
     */
    @Override
    public void output(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        if (thingsRequest.getTarget() instanceof ThingsSofaBus thingsSofaBus) {
            thingsSofaBus.output(thingsRequest, thingsResponse);
        } else {
            Set<ThingsSofaBus> thingsSofaBus = thingsSofaBusManager.getAllSofaBus();
            for (ThingsSofaBus bus : thingsSofaBus) {
                if (bus.isStarted()) {
                    bus.output(thingsRequest, thingsResponse);
                }
            }
        }
    }
}
