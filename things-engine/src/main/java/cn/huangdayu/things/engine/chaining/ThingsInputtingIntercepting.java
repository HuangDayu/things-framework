package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsIntercepting;
import cn.huangdayu.things.common.annotation.ThingsInterceptor;
import cn.huangdayu.things.common.async.ThingsAsyncManager;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_SEPARATOR;
import static cn.huangdayu.things.common.enums.ThingsChainingType.INPUTTING;
import static cn.huangdayu.things.engine.chaining.ThingsOutputtingIntercepting.OUTPUTTING_MESSAGES_CACHE;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsInterceptor(order = Integer.MIN_VALUE, chainingType = INPUTTING)
public class ThingsInputtingIntercepting implements ThingsIntercepting {


    /**
     * 先进先出缓存，防止重复处理消息
     */
    private static final Cache<String, String> INTPUTTING_MESSAGES_CACHE = new FIFOCache<>(1000);

    @Override
    public boolean preHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        // 如果消息为空，则直接返回false
        if (thingsRequest == null || thingsRequest.getJtm() == null) {
            return false;
        }
        // 如果是回复，则将其加入异步处理，不进行下一步处理
        if (thingsResponse.getJtm().isResponse()) {
            ThingsAsyncManager.asAsyncResponse(thingsResponse);
            return false;
        }
        if (thingsRequest.getJtm().isResponse()) {
            thingsResponse.setJtm(thingsRequest.getJtm());
            ThingsAsyncManager.asAsyncResponse(thingsResponse);
            return false;
        }
        // 如果是自己发出的消息，并且不是回复消息，则直接返回false
        if (OUTPUTTING_MESSAGES_CACHE.get(thingsRequest.getJtm().getId()) != null && !thingsRequest.getJtm().isResponse()) {
            return false;
        }
        // 如果产品码为空，则直接返回false
        if (StrUtil.isBlank(thingsRequest.getJtm().getBaseMetadata().getProductCode())) {
            return false;
        }
        // 如果已经处理过则不再处理
        if (INTPUTTING_MESSAGES_CACHE.containsKey(thingsRequest.getJtm().getId())) {
            return false;
        }
        INTPUTTING_MESSAGES_CACHE.put(thingsRequest.getJtm().getId(), THINGS_SEPARATOR, TimeUnit.MINUTES.toMillis(5));
        return true;
    }

    @Override
    public void afterCompletion(ThingsRequest thingsRequest, ThingsResponse thingsResponse, Exception exception) {
        log.debug("Things inputting message, times: {} , SofaBus type: {} , groupId: {} clientId: {} , topic: {} , sessionCode: {} , request： {} , response: {}  , exception: ",
                System.currentTimeMillis() - thingsRequest.getJtm().getTime(), thingsRequest.getType(), thingsRequest.getGroupCode(), thingsRequest.getClientCode(),
                thingsRequest.getTopic(), thingsRequest.getSessionCode(), thingsRequest.getJtm(), thingsRequest.getJtm(), exception);
    }
}
