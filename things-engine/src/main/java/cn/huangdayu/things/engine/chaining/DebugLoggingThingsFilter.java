package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.filters.ThingsFilter;
import cn.huangdayu.things.api.filters.ThingsFilterChain;
import cn.huangdayu.things.common.annotation.ThingsFiltering;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsFiltering
public class DebugLoggingThingsFilter implements ThingsFilter {
    @Override
    public void doFilter(ThingsRequest thingsRequest, ThingsResponse thingsResponse, ThingsFilterChain thingsFilterChain) {
        log.debug("Things debug logging , times: {} , request： {} , response: {}",
                System.currentTimeMillis() - thingsRequest.getMessage().getTime(), thingsRequest.getMessage(), thingsResponse.getMessage());
        thingsFilterChain.doFilter(thingsRequest, thingsResponse);
    }

}
