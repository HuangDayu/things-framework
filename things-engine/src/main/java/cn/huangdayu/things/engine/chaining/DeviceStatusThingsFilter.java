package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.filters.ThingsFilter;
import cn.huangdayu.things.api.filters.ThingsFilterChain;
import cn.huangdayu.things.common.event.ThingsEventObserver;
import cn.huangdayu.things.common.event.ThingsSessionStatusEvent;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.common.wrapper.ThingsSession;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
public abstract class DeviceStatusThingsFilter implements ThingsFilter {

    private final ThingsEventObserver thingsEventObserver;

    @Override
    public void doFilter(ThingsRequest thingsRequest, ThingsResponse thingsResponse, ThingsFilterChain thingsFilterChain) {
        JsonThingsMessage message = thingsRequest.getMessage();
        BaseThingsMetadata baseMetadata = message.getBaseMetadata();
        ThingsSession thingsSession = new ThingsSession();
        thingsSession.setDeviceCode(baseMetadata.getDeviceCode());
        thingsSession.setOnline(status());
        thingsSession.setOnlineTime(System.currentTimeMillis());
        thingsSession.setProductCode(baseMetadata.getProductCode());
        thingsSession.setSessionCode(StrUtil.toString(ReflectUtil.getFieldValue(message.getPayload(), "sessionCode")));
        thingsEventObserver.notifyObservers(new ThingsSessionStatusEvent(this, thingsSession));
    }

    abstract boolean status();

}
