package cn.huangdayu.things.sofabiz.multiple;

import cn.huangdayu.things.api.message.ThingsHandling;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusPublisher;
import cn.huangdayu.things.common.annotation.ThingsHandler;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.sofabiz.condition.ThingsSofaBizMultipleCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;

import static cn.huangdayu.things.common.enums.ThingsChainingType.OUTPUTTING;
import static cn.huangdayu.things.sofabiz.ThingsSofaBizUtils.getArkService;

/**
 * @author huangdayu
 */
@Conditional(ThingsSofaBizMultipleCondition.class)
@RequiredArgsConstructor
@ThingsHandler(order = 1, chainingType = OUTPUTTING)
public class ThingsSofaBizOutputting implements ThingsHandling {


    private volatile static ThingsSofaBusPublisher thingsSofaBusPublisher;

    @Override
    public boolean canHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        return true;
    }

    @Override
    public void doHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        getThingsSofaBusPublisher().output(thingsRequest, thingsResponse);
    }

    public static ThingsSofaBusPublisher getThingsSofaBusPublisher() {
        if (thingsSofaBusPublisher == null) {
            thingsSofaBusPublisher = getArkService(ThingsSofaBusPublisher.class);
        }
        return thingsSofaBusPublisher;
    }

}
