package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsFiltering;
import cn.huangdayu.things.common.annotation.ThingsFilter;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import lombok.extern.slf4j.Slf4j;

import static cn.huangdayu.things.common.enums.ThingsStreamingType.INPUTTING;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsFilter(source = INPUTTING)
public class ThingsDebugFiltering implements ThingsFiltering {
    @Override
    public void doFilter(ThingsRequest thingsRequest, ThingsResponse thingsResponse, Chain chain) {
        chain.doFilter(thingsRequest, thingsResponse);
    }

}
