package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.engine.async.ThingsSessionStatusEvent;
import cn.huangdayu.things.engine.chaining.filters.Filter;
import cn.huangdayu.things.engine.chaining.filters.FilterChain;
import cn.huangdayu.things.engine.core.ThingsObserverEngine;
import cn.huangdayu.things.engine.message.BaseThingsMetadata;
import cn.huangdayu.things.engine.message.JsonThingsMessage;
import cn.huangdayu.things.engine.wrapper.ThingsRequest;
import cn.huangdayu.things.engine.wrapper.ThingsResponse;
import cn.huangdayu.things.engine.wrapper.ThingsSession;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
public abstract class DeviceStatusFilter implements Filter {

    private final ThingsObserverEngine thingsObserverEngine;

    @Override
    public void doFilter(ThingsRequest thingsRequest, ThingsResponse thingsResponse, FilterChain filterChain) {
        JsonThingsMessage message = thingsRequest.getMessage();
        BaseThingsMetadata baseMetadata = message.getBaseMetadata();
        ThingsSession thingsSession = new ThingsSession();
        thingsSession.setDeviceCode(baseMetadata.getDeviceCode());
        thingsSession.setOnline(status());
        thingsSession.setOnlineTime(System.currentTimeMillis());
        thingsSession.setProductCode(baseMetadata.getProductCode());
        thingsSession.setSessionCode(StrUtil.toString(ReflectUtil.getFieldValue(message.getPayload(), "sessionCode")));
        thingsObserverEngine.notifyObservers(new ThingsSessionStatusEvent(this, thingsSession));
    }

    abstract boolean status();

}
