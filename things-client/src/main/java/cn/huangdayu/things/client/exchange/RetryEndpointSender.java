package cn.huangdayu.things.client.exchange;

import cn.huangdayu.things.api.endpoint.ThingsEndpointSender;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.EndpointProtocolType;
import cn.huangdayu.things.common.event.ThingsCacheMessageEvent;
import cn.huangdayu.things.common.event.ThingsEventObserver;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.common.enums.EndpointProtocolType.RETRY;

/**
 * TODO huangdayu 2024-09-02 消息缓存应当支持持久化，防止消息丢失
 *
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class RetryEndpointSender implements ThingsEndpointSender {

    public static final Cache<String, JsonThingsMessage> TIMED_CACHE = CacheUtil.newTimedCache(60 * 1000);
    public final ThingsEventObserver thingsEventObserver;


    @PostConstruct
    public void init() {
        TIMED_CACHE.setListener((key, cachedObject) -> {
            thingsEventObserver.notifyObservers(new ThingsCacheMessageEvent(this, cachedObject));
        });
    }

    @Override
    public EndpointProtocolType endpointProtocol() {
        return RETRY;
    }

    @Override
    public JsonThingsMessage handler(String endpointUri, JsonThingsMessage message) {
        TIMED_CACHE.put(message.getId(), message, 30 * 1000);
        return message.async();
    }

}
