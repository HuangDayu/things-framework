package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusInputting;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import lombok.RequiredArgsConstructor;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsSofaBusInputHandling implements ThingsSofaBusInputting {
    private final ThingsChaining thingsChaining;

    public boolean input(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        return thingsChaining.input(thingsRequest, thingsResponse);
    }
}
