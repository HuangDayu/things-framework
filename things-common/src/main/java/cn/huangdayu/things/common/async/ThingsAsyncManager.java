package cn.huangdayu.things.common.async;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsAsync;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author huangdayu
 */
public class ThingsAsyncManager {

    private static final Cache<String, ThingsAsync> THINGS_ASYNC_CACHE = CacheUtil.newTimedCache(20 * 1000);


    static {
        THINGS_ASYNC_CACHE.setListener((key, thingsAsync) -> {
            if (!thingsAsync.isCompleted()) {
                asAsyncResponse(thingsAsync, thingsAsync.getThingsRequest().getJtm().timeout());
            }
        });
    }

    public static boolean asAsyncRequest(ThingsRequest request, ThingsResponse response) {
        if (response.getConsumer() == null && response.getFuture() == null) {
            return false;
        }
        if (THINGS_ASYNC_CACHE.containsKey(request.getJtm().getId())) {
            return true;
        }
        ThingsAsync thingsAsync = new ThingsAsync(request.getJtm().getId(), request.getJtm().getTimeout(), false, request, response);
        THINGS_ASYNC_CACHE.put(thingsAsync.getAsyncId(), thingsAsync, thingsAsync.getTimeout());
        return true;
    }

    public static boolean asAsyncResponse(ThingsRequest request, ThingsResponse response) {
        JsonThingsMessage jtm = request.getJtm();
        if (jtm != null && StrUtil.isNotBlank(jtm.getBaseMetadata().getErrorCode())) {
            ThingsAsync thingsAsync = THINGS_ASYNC_CACHE.get(jtm.getId());
            if (thingsAsync != null) {
                asAsyncResponse(thingsAsync, jtm);
                return true;
            }
        }
        return false;
    }

    private static void asAsyncResponse(ThingsAsync thingsAsync, JsonThingsMessage jtm) {
        ThingsResponse thingsResponse = thingsAsync.getThingsResponse();
        thingsResponse.setJtm(jtm);
        asAsyncResponse(thingsResponse);
        thingsAsync.setCompleted(true);
    }

    public static void asAsyncResponse(ThingsResponse thingsResponse) {
        if (thingsResponse.getJtm() != null) {
            if (thingsResponse.getConsumer() != null) {
                thingsResponse.getConsumer().accept(thingsResponse);
            }
            if (thingsResponse.getFuture() != null) {
                thingsResponse.getFuture().complete(thingsResponse);
            }
        }
    }

}
