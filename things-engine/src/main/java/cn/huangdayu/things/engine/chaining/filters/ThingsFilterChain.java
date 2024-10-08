package cn.huangdayu.things.engine.chaining.filters;

import cn.huangdayu.things.engine.wrapper.ThingsRequest;
import cn.huangdayu.things.engine.wrapper.ThingsResponse;

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
