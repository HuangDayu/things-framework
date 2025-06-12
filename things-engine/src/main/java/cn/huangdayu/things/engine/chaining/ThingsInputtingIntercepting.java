package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsIntercepting;
import cn.huangdayu.things.common.annotation.ThingsInterceptor;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

import static cn.huangdayu.things.common.async.ThingsAsyncManager.hasAsyncRequest;
import static cn.huangdayu.things.common.enums.ThingsChainingType.INPUTTING;

/**
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
@ThingsInterceptor(order = Integer.MIN_VALUE, chainingType = INPUTTING)
public class ThingsInputtingIntercepting implements ThingsIntercepting {

    @Override
    public boolean preHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        // 如果消息为空，则直接返回false
        if (thingsRequest == null || thingsRequest.getJtm() == null) {
            return false;
        }
        // 如果是回复，则将其加入异步处理，不进行下一步处理
        if (thingsRequest.getJtm().isResponse() && !hasAsyncRequest(thingsRequest.getJtm().getId())) {
            return false;
        }
        // 如果产品码为空，则直接返回false
        return !StrUtil.isBlank(thingsRequest.getJtm().getBaseMetadata().getProductCode());
    }


    @Override
    public void afterCompletion(ThingsRequest thingsRequest, ThingsResponse thingsResponse, Exception exception) {
        log.atLevel(exception != null ? Level.WARN : Level.DEBUG).log("Things inputting message, times: {} , SofaBus type: {} , groupId: {} clientId: {} , topic: {} , sessionCode: {} , request： {} , response: {}  , exception: {}",
                System.currentTimeMillis() - thingsRequest.getJtm().getTime(), thingsRequest.getType(), thingsRequest.getGroupCode(), thingsRequest.getClientCode(),
                thingsRequest.getTopic(), thingsRequest.getSessionCode(), thingsRequest.getJtm(), thingsResponse.getJtm(), exception instanceof ThingsException ? exception.getMessage() : exception);
    }
}
