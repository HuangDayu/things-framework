package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;

import java.util.List;

public interface ThingsFiltering {

    void doFilter(ThingsRequest thingsRequest, ThingsResponse thingsResponse, Chain chain);

    class Chain {
        private final List<ThingsFiltering> thingsFilters;
        private int index;

        public Chain(List<ThingsFiltering> thingsFilters) {
            this.thingsFilters = thingsFilters;
        }


        public void doFilter(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
            if (index < thingsFilters.size()) {
                ThingsFiltering currentThingsFiltering = thingsFilters.get(index);
                index++;
                currentThingsFiltering.doFilter(thingsRequest, thingsResponse, this);
            }
        }
    }
}
