package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.engine.wrapper.*;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangdayu
 */
@Slf4j
public abstract class ThingsEngineBaseExecutor {


    /**
     * identifier vs productCode vs ThingsHandleWrapper
     */
    protected static final Table<String, String, ThingsFunction> THINGS_SERVICES_TABLE = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);

    /**
     * productCode vs ThingsProperties
     */
    protected static final Map<String, ThingsProperties> PRODUCT_PROPERTY_MAP = new ConcurrentHashMap<>();

    /**
     * deviceCode vs productCode vs ThingsProperties
     */
    protected static final Table<String, String, ThingsProperties> DEVICE_PROPERTY_MAP = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);


    /**
     * identifier vs productCode vs ThingsEvents
     */
    protected static final Table<String, String, ThingsEvents> THINGS_EVENTS_TABLE = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);


    /**
     * identifier vs productCode vs ThingsEventListener
     */
    protected static final Table<String, String, Set<ThingsFunction>> THINGS_EVENTS_LISTENER_TABLE = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);


    /**
     * identifier vs productCode vs ThingsPropertyListener
     */
    protected static final Table<String, String, Set<ThingsFunction>> THINGS_PROPERTY_LISTENER_TABLE = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);


    /**
     * identifier vs productCode vs ThingsFilters
     */
    protected static final Table<String, String, Set<ThingsFilters>> THINGS_FILTERS_TABLE = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);

    /**
     * identifier vs productCode vs ThingsInterceptors
     */
    protected static final Table<String, String, Set<ThingsInterceptors>> THINGS_REQUEST_INTERCEPTORS_TABLE = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);

    /**
     * identifier vs productCode vs ThingsInterceptors
     */
    protected static final Table<String, String, Set<ThingsInterceptors>> THINGS_RESPONSE_INTERCEPTORS_TABLE = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);
}
