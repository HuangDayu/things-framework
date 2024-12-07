package cn.huangdayu.things.api.message;


import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;

import java.util.List;

public class ThingsFilterChain {
    private final List<ThingsFilter> thingsFilters;
    private int index;

    public ThingsFilterChain(List<ThingsFilter> thingsFilters) {
        this.thingsFilters = thingsFilters;
    }


    public void doFilter(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        if (index < thingsFilters.size()) {
            ThingsFilter currentThingsFilter = thingsFilters.get(index);
            index++;
            currentThingsFilter.doFilter(thingsRequest, thingsResponse, this);
        }
    }


}
