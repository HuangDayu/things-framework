package cn.huangdayu.things.engine.chaining.filters;

import cn.huangdayu.things.engine.wrapper.ThingsRequest;
import cn.huangdayu.things.engine.wrapper.ThingsResponse;

import java.util.List;

public class FilterChain {
    private final List<Filter> filters;
    private int index;

    public FilterChain(List<Filter> filters) {
        this.filters = filters;
    }


    public void doFilter(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        if (index < filters.size()) {
            Filter currentFilter = filters.get(index);
            index++;
            currentFilter.doFilter(thingsRequest, thingsResponse, this);
        }
    }


}
