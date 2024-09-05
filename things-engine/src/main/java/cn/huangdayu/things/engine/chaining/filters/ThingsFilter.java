package cn.huangdayu.things.engine.chaining.filters;

import cn.huangdayu.things.engine.wrapper.ThingsRequest;
import cn.huangdayu.things.engine.wrapper.ThingsResponse;

public interface ThingsFilter {

    void doFilter(ThingsRequest thingsRequest, ThingsResponse thingsResponse, ThingsFilterChain thingsFilterChain);

}
