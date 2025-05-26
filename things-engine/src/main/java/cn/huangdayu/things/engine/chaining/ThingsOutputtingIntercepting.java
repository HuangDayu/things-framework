package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsIntercepting;
import cn.huangdayu.things.common.annotation.ThingsInterceptor;
import cn.huangdayu.things.common.async.ThingsAsyncManager;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.FIFOCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_SEPARATOR;
import static cn.huangdayu.things.common.enums.ThingsChainingType.OUTPUTTING;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsInterceptor(order = Integer.MIN_VALUE, chainingType = OUTPUTTING)
public class ThingsOutputtingIntercepting implements ThingsIntercepting {

    public static final Cache<String, String> OUTPUTTING_MESSAGES_CACHE = new FIFOCache<>(1000);

    @Override
    public void afterCompletion(ThingsRequest thingsRequest, ThingsResponse thingsResponse, Exception exception) {
        log.debug("Things outputting , times: {} , request： {} , response: {} , exception: ",
                System.currentTimeMillis() - thingsRequest.getJtm().getTime(), thingsRequest.getJtm(), thingsRequest.getJtm(), exception);
        ThingsAsyncManager.asAsyncRequest(thingsRequest, thingsResponse);
        OUTPUTTING_MESSAGES_CACHE.put(thingsRequest.getJtm().getId(), THINGS_SEPARATOR, TimeUnit.MINUTES.toMillis(5));
    }
}
