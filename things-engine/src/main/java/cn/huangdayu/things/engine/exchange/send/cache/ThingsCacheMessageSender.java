package cn.huangdayu.things.engine.exchange.send.cache;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.async.ThingsCacheMessageEvent;
import cn.huangdayu.things.engine.core.ThingsObserverEngine;
import cn.huangdayu.things.engine.exchange.send.ThingsMessageSender;
import cn.huangdayu.things.engine.message.JsonThingsMessage;
import cn.huangdayu.things.engine.wrapper.ThingsInstance;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.engine.exchange.ThingsExchangeExecutor.CACHE;

/**
 * TODO huangdayu 2024-09-02 消息缓存应当支持持久化，防止消息丢失
 *
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsCacheMessageSender implements ThingsMessageSender {

    public static final Cache<String, JsonThingsMessage> TIMED_CACHE = CacheUtil.newTimedCache(60 * 1000);
    public final ThingsObserverEngine thingsObserverEngine;


    @PostConstruct
    public void init() {
        TIMED_CACHE.setListener((key, cachedObject) -> {
            thingsObserverEngine.notifyObservers(new ThingsCacheMessageEvent(this, cachedObject));
        });
    }

    @Override
    public String getProtocol() {
        return CACHE;
    }

    @Override
    public JsonThingsMessage handler(ThingsInstance thingsInstance, JsonThingsMessage message) {
        TIMED_CACHE.put(message.getId(), message, 30 * 1000);
        return message.async();
    }
}
