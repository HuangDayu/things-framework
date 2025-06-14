package cn.huangdayu.things.starter.engine;

import cn.huangdayu.things.api.infrastructure.ThingsCacheService;
import cn.huangdayu.things.common.annotation.ThingsBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsSpringCacheService implements ThingsCacheService {


    private final Cache cache;

    public ThingsSpringCacheService(CacheManager cacheManager) {
        this.cache = cacheManager.getCache("things-engine-map-local-cache");
    }

    @Override
    public void put(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public Object putIfAbsent(Object key, Object value) {
        Cache.ValueWrapper valueWrapper = cache.putIfAbsent(key, value);
        if (valueWrapper != null) {
            return valueWrapper.get();
        }
        return null;
    }

    @Override
    public Object get(String key) {
        Cache.ValueWrapper valueWrapper = cache.get(key);
        if (valueWrapper != null) {
            return valueWrapper.get();
        }
        return null;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return cache.get(key, type);
    }

    @Override
    public void remove(String key) {
        cache.evictIfPresent(key);
    }

    @Override
    public void removeAll() {
        cache.clear();
    }

    @Override
    public boolean containsKey(String key) {
        return cache.get(key) != null;
    }

}
