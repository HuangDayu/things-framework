package cn.huangdayu.things.sofabiz;

import cn.huangdayu.things.api.message.ThingsHandling;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusPublisher;
import cn.huangdayu.things.common.annotation.ThingsHandler;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import com.alipay.sofa.koupleless.common.api.SpringServiceFinder;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.common.enums.ThingsChainingType.OUTPUTTING;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsHandler(order = 1, chainingType = OUTPUTTING)
public class ThingsSofaBizOutputHandling implements ThingsHandling {

    @Override
    public boolean canHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        return true;
    }

    @Override
    public void doHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        ThingsSofaBusPublisher thingsSofaBusPublisher = SpringServiceFinder.getBaseService(ThingsSofaBusPublisher.class);
        thingsSofaBusPublisher.output(thingsRequest, thingsResponse);
    }

}
