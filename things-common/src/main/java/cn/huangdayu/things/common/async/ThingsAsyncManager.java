package cn.huangdayu.things.common.async;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsAsync;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;

/**
 * @author huangdayu
 */
public class ThingsAsyncManager {

    private static final Cache<String, ThingsAsync> THINGS_ASYNC_CACHE = CacheUtil.newTimedCache(20 * 1000);


    static {
        THINGS_ASYNC_CACHE.setListener((key, thingsAsync) -> {
            if (!thingsAsync.isCompleted()) {
                asAsyncResponse(new ThingsResponse(thingsAsync.getThingsRequest().getJtm().timeout()));
            }
        });
    }

    public static void asAsyncRequest(ThingsRequest thingsRequest) {
        if (thingsRequest.getResponseConsumer() == null && thingsRequest.getResponseFuture() == null) {
            return;
        }
        if (THINGS_ASYNC_CACHE.containsKey(thingsRequest.getJtm().getId())) {
            return;
        }
        ThingsAsync thingsAsync = new ThingsAsync(thingsRequest.getJtm().getId(), thingsRequest.getJtm().getTimeout(), false, thingsRequest, null);
        THINGS_ASYNC_CACHE.put(thingsAsync.getAsyncId(), thingsAsync, thingsAsync.getTimeout());
    }

    public static void asAsyncResponse(ThingsResponse thingsResponse) {
        JsonThingsMessage jtm = thingsResponse.getJtm();
        if (jtm != null && jtm.isResponse()) {
            ThingsAsync thingsAsync = THINGS_ASYNC_CACHE.get(jtm.getId());
            if (thingsAsync != null) {
                ThingsRequest thingsRequest = thingsAsync.getThingsRequest();
                if (thingsRequest.getResponseConsumer() != null) {
                    thingsRequest.getResponseConsumer().accept(thingsResponse);
                }
                if (thingsRequest.getResponseFuture() != null) {
                    thingsRequest.getResponseFuture().complete(thingsResponse);
                }
                thingsAsync.setThingsResponse(thingsResponse);
                thingsAsync.setCompleted(true);
            }
        }
    }

    public static boolean hasAsyncRequest(String jtmId) {
        return THINGS_ASYNC_CACHE.containsKey(jtmId);
    }

}
