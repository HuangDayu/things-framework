package cn.huangdayu.things.engine.async;

import cn.huangdayu.things.common.message.AsyncThingsMessage;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author huangdayu
 */
public class ThingsAsyncManager {

    private static final Cache<String, AsyncThingsMessage> THINGS_ASYNC_CACHE = CacheUtil.newTimedCache(20 * 1000);


    static {
        THINGS_ASYNC_CACHE.setListener((key, asyncThingsMessage) -> {
            if (asyncThingsMessage.getReplied() == 0) {
                asyncThingsMessage.getResponseConsumer().accept(asyncThingsMessage.timeout());
            }
        });
    }

    public static void asyncRequest(AsyncThingsMessage asyncThingsMessage) {
        if (!THINGS_ASYNC_CACHE.containsKey(asyncThingsMessage.getId())) {
            THINGS_ASYNC_CACHE.put(asyncThingsMessage.getId(), asyncThingsMessage, asyncThingsMessage.getTimeout());
        }
    }

    public static boolean asyncResponse(JsonThingsMessage jsonThingsMessage) {
        if (jsonThingsMessage != null) {
            if (StrUtil.isNotBlank(jsonThingsMessage.getBaseMetadata().getErrorCode())) {
                AsyncThingsMessage asyncThingsMessage = THINGS_ASYNC_CACHE.get(jsonThingsMessage.getId());
                if (asyncThingsMessage != null) {
                    asyncThingsMessage.getResponseConsumer().accept(jsonThingsMessage);
                    asyncThingsMessage.setReplied(asyncThingsMessage.getReplied() + 1);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasAsync(JsonThingsMessage jsonThingsMessage) {
        return THINGS_ASYNC_CACHE.get(jsonThingsMessage.getId()) != null;
    }
}
