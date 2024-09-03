package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.engine.annotation.ThingsFilter;
import cn.huangdayu.things.engine.chaining.filters.Filter;
import cn.huangdayu.things.engine.chaining.filters.FilterChain;
import cn.huangdayu.things.engine.wrapper.ThingsRequest;
import cn.huangdayu.things.engine.wrapper.ThingsResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsFilter
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ThingsRequest thingsRequest, ThingsResponse thingsResponse, FilterChain filterChain) {
        log.debug("Things debug logging , times: {} , request： {} , response: {}",
                System.currentTimeMillis() - thingsRequest.getMessage().getTime(), thingsRequest.getMessage(), thingsResponse.getMessage());
        filterChain.doFilter(thingsRequest, thingsResponse);
    }

}
