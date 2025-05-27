package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsIntercepting;
import cn.huangdayu.things.common.annotation.ThingsInterceptor;
import cn.huangdayu.things.common.async.ThingsAsyncManager;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.observer.event.ThingsSessionUpdatedEvent;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.common.wrapper.ThingsServlet;
import cn.huangdayu.things.common.wrapper.ThingsSession;
import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_SEPARATOR;
import static cn.huangdayu.things.common.enums.ThingsChainingType.OUTPUTTING;

/**
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
@ThingsInterceptor(order = Integer.MIN_VALUE, chainingType = OUTPUTTING)
public class ThingsOutputtingIntercepting implements ThingsIntercepting {

    private final ThingsEventObserver thingsEventObserver;

    public static final Cache<String, String> OUTPUTTING_MESSAGES_CACHE = new FIFOCache<>(1000);

    @Override
    public void afterCompletion(ThingsRequest thingsRequest, ThingsResponse thingsResponse, Exception exception) {
        ThingsServlet thingsServlet = thingsResponse != null && thingsResponse.getJtm() != null ? thingsResponse : thingsRequest;
        log.debug("Things outputting message, times: {} , SofaBus type: {} , groupId: {} clientId: {} , topic: {} , sessionCode: {} , request： {} , response: {}  , exception: ",
                System.currentTimeMillis() - thingsServlet.getJtm().getTime(), thingsServlet.getType(), thingsServlet.getGroupCode(), thingsServlet.getClientCode(),
                thingsServlet.getTopic(), thingsServlet.getSessionCode(), thingsServlet.getJtm(), thingsServlet.getJtm(), exception);
        ThingsAsyncManager.asAsyncRequest(thingsRequest);
        OUTPUTTING_MESSAGES_CACHE.put(thingsServlet.getJtm().getId(), THINGS_SEPARATOR, TimeUnit.MINUTES.toMillis(5));
    }


    public void doFilter(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        JsonThingsMessage jtm = thingsRequest.getJtm();
        BaseThingsMetadata baseMetadata = jtm.getBaseMetadata();
        ThingsSession thingsSession = new ThingsSession();
        thingsSession.setDeviceCode(baseMetadata.getDeviceCode());
        thingsSession.setOnline(true);
        thingsSession.setTime(jtm.getTime());
        thingsSession.setProductCode(baseMetadata.getProductCode());
        thingsSession.setSessionCode(StrUtil.toString(ReflectUtil.getFieldValue(jtm.getPayload(), "sessionCode")));
        thingsEventObserver.notifyObservers(new ThingsSessionUpdatedEvent(this, thingsSession));
    }
}
