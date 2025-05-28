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
import cn.huangdayu.things.common.wrapper.ThingsSession;
import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

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

    @Override
    public void afterCompletion(ThingsRequest thingsRequest, ThingsResponse thingsResponse, Exception exception) {
        log.atLevel(exception != null ? Level.WARN : Level.DEBUG).log("Things outputting message, times: {} , SofaBus type: {} , groupId: {} clientId: {} , topic: {} , sessionCode: {} , request： {} , response: {}  , exception: {}",
                System.currentTimeMillis() - thingsRequest.getJtm().getTime(), thingsRequest.getType(), thingsRequest.getGroupCode(), thingsRequest.getClientCode(),
                thingsRequest.getTopic(), thingsRequest.getSessionCode(), thingsRequest.getJtm(), thingsResponse.getJtm(), exception != null ? exception.getMessage() : "");
        ThingsAsyncManager.asAsyncRequest(thingsRequest);
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
