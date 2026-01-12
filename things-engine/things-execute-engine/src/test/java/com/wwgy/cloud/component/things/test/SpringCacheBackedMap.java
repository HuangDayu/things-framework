//package cn.huangdayu.things.engine.test;
//
//import org.springframework.cache.Cache;
//import org.springframework.cache.Cache.ValueWrapper;
//import org.springframework.cache.CacheManager;
//
//import java.util.*;
//
//public class SpringCacheBackedMap<K, V> extends AbstractMap<K, V> {
//
//    private final CacheManager cacheManager;
//    private final String cacheName;
//
//    public SpringCacheBackedMap(CacheManager cacheManager, String cacheName) {
//        this.cacheManager = cacheManager;
//        this.cacheName = cacheName;
//    }
//
//    private Cache getCache() {
//        return cacheManager.getCache(cacheName);
//    }
//
//    @Override
//    public int size() {
//        Cache cache = getCache();
//        if (cache == null) {
//            return 0;
//        }
//        return -1;
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return size() == 0;
//    }
//
//    @Override
//    public boolean containsKey(Object key) {
//        Cache cache = getCache();
//        if (cache == null) {
//            return false;
//        }
//        return cache.get(key) != null;
//    }
//
//    @Override
//    public V get(Object key) {
//        Cache cache = getCache();
//        if (cache == null) {
//            return null;
//        }
//        ValueWrapper valueWrapper = cache.get(key);
//        return valueWrapper != null ? (V) valueWrapper.get() : null;
//    }
//
//    @Override
//    public V put(K key, V value) {
//        Cache cache = getCache();
//        if (cache == null) {
//            return null;
//        }
//        ValueWrapper previousValue = cache.putIfAbsent(key, value);
//        return previousValue != null ? (V) previousValue.get() : null;
//    }
//
//    @Override
//    public V remove(Object key) {
//        Cache cache = getCache();
//        if (cache == null) {
//            return null;
//        }
//        ValueWrapper valueWrapper = cache.get(key);
//        if (valueWrapper != null) {
//            cache.evict(key);
//            return (V) valueWrapper.get();
//        }
//        return null;
//    }
//
//    @Override
//    public void putAll(Map<? extends K, ? extends V> m) {
//        Cache cache = getCache();
//        if (cache == null) {
//            return;
//        }
//        m.forEach((k, v) -> cache.put(k, v));
//    }
//
//    @Override
//    public void clear() {
//        Cache cache = getCache();
//        if (cache == null) {
//            return;
//        }
//        cache.clear();
//    }
//
//    @Override
//    public Set<Entry<K, V>> entrySet() {
//
//        Cache cache = getCache();
//        if (cache == null) {
//            return Collections.emptySet();
//        }
//
//        Object nativeCache = cache.getNativeCache();
//        if (nativeCache instanceof Map) {
//            return ((Map) nativeCache).entrySet();
//        }
//
//        return new AbstractSet<Entry<K, V>>() {
//            @Override
//            public Iterator<Entry<K, V>> iterator() {
//                return new Iterator<Entry<K, V>>() {
//                    private final Iterator<Map.Entry<Object, ValueWrapper>> delegate = (Iterator) getCache().getNativeCache();
//
//                    @Override
//                    public boolean hasNext() {
//                        return delegate.hasNext();
//                    }
//
//                    @Override
//                    public Entry<K, V> next() {
//                        Map.Entry<Object, ValueWrapper> entry = delegate.next();
//                        return new AbstractMap.SimpleEntry<>((K) entry.getKey(), (V) entry.getValue().get());
//                    }
//                };
//            }
//
//            @Override
//            public int size() {
//                return size();
//            }
//        };
//    }
//}
