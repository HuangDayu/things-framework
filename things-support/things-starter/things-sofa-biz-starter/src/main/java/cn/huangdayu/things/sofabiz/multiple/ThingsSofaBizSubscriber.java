package cn.huangdayu.things.sofabiz.multiple;

import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.message.ThingsSubscriber;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.sofabiz.condition.ThingsSofaBizMultipleCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;

/**
 * @author huangdayu
 */
@Conditional(ThingsSofaBizMultipleCondition.class)
@ThingsBean
@RequiredArgsConstructor
public class ThingsSofaBizSubscriber implements ThingsSubscriber {

    private final ThingsChaining thingsChaining;

    @Override
    public boolean input(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        return thingsChaining.input(thingsRequest, thingsResponse);
    }
}
