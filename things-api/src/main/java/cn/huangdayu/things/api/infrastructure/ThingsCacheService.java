package cn.huangdayu.things.api.infrastructure;


/**
 * @author huangdayu
 */
public interface ThingsCacheService {

    void put(String key, Object value);


    Object putIfAbsent(Object key, Object value);


    Object get(String key);


    <T> T get(Object key, Class<T> type);


    void remove(String key);


    void removeAll();


    boolean containsKey(String key);


}
