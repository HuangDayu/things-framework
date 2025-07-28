package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsIntercepting;
import cn.huangdayu.things.common.annotation.ThingsInterceptor;
import cn.huangdayu.things.common.async.ThingsAsyncManager;
import cn.huangdayu.things.common.events.ThingsSessionUpdatedEvent;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.ThingsMessageMethod;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.common.wrapper.ThingsSession;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

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
                System.currentTimeMillis() - thingsRequest.getTrm().getTime(), thingsRequest.getType(), thingsRequest.getGroupCode(), thingsRequest.getClientCode(),
                thingsRequest.getTopic(), thingsRequest.getSessionCode(), thingsRequest.getTrm(), thingsResponse.getTrm(), exception instanceof ThingsException ? exception.getMessage() : exception);
        ThingsAsyncManager.asAsyncRequest(thingsRequest);
    }


    public void doFilter(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        ThingsRequestMessage trm = thingsRequest.getTrm();
        ThingsMessageMethod messageMethod = trm.getMessageMethod();
        ThingsSession thingsSession = new ThingsSession();
        thingsSession.setDeviceCode(messageMethod.getDeviceCode());
        thingsSession.setOnline(true);
        thingsSession.setTime(trm.getTime());
        thingsSession.setProductCode(messageMethod.getProductCode());
        thingsSession.setSessionCode(StrUtil.toString(ReflectUtil.getFieldValue(trm.getParams(), "sessionCode")));
        thingsEventObserver.notifyObservers(new ThingsSessionUpdatedEvent(this, thingsSession));
    }
}
