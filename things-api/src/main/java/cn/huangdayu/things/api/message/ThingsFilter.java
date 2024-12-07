package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;

public interface ThingsFilter {

    void doFilter(ThingsRequest thingsRequest, ThingsResponse thingsResponse, ThingsFilterChain thingsFilterChain);

}
