package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsIntercepting;
import cn.huangdayu.things.common.annotation.ThingsInterceptor;
import cn.huangdayu.things.common.async.ThingsAsyncManager;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.BAD_REQUEST;
import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_SEPARATOR;
import static cn.huangdayu.things.common.enums.ThingsStreamingType.INPUTTING;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsInterceptor(order = Integer.MIN_VALUE, source = INPUTTING)
public class ThingsInputtingIntercepting implements ThingsIntercepting {


    /**
     * 先进先出缓存，防止重复处理消息
     */
    private static final Cache<String, String> HANDLED_MESSAGES_CACHE = new FIFOCache<>(1000);

    @Override
    public boolean preHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        if (StrUtil.isBlank(thingsRequest.getJtm().getBaseMetadata().getProductCode())) {
            throw new ThingsException(thingsRequest.getJtm(), BAD_REQUEST, "Things message not has productCode.");
        }
        if (HANDLED_MESSAGES_CACHE.containsKey(thingsRequest.getJtm().getId())) {
            log.warn("Things inputting repeat message : {}", thingsRequest.getJtm());
            return false;
        }
        HANDLED_MESSAGES_CACHE.put(thingsRequest.getJtm().getId(), THINGS_SEPARATOR, TimeUnit.MINUTES.toMillis(5));
        return true;
    }

    @Override
    public void postHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        ThingsAsyncManager.asAsyncResponse(thingsResponse);
    }

    @Override
    public void afterCompletion(ThingsRequest thingsRequest, ThingsResponse thingsResponse, Exception exception) {
        log.debug("Things inputting , times: {} , request： {} , response: {}  , exception: ",
                System.currentTimeMillis() - thingsRequest.getJtm().getTime(), thingsRequest.getJtm(), thingsRequest.getJtm(), exception);
    }
}
