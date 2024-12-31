package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.container.ThingsContainer;
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
public abstract class ThingsBaseExecutor {
    /**
     * containerName vs ThingsContainer
     */
    protected static final Map<String, ThingsContainer> THINGS_CONTAINERS = new ConcurrentHashMap<>();


    /**
     * productCode vs ThingsBeanClass vs ThingsEntity
     */
    protected static final Table<String, Class<?>, ThingsEntity> THINGS_ENTITY_TABLE = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);


    /**
     * identifier vs productCode vs ThingsFunction
     */
    protected static final Table<String, String, ThingsFunction> THINGS_SERVICES_TABLE = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);

    /**
     * productCode vs ThingsProperties
     */
    protected static final Map<String, ThingsProperty> PRODUCT_PROPERTY_MAP = new ConcurrentHashMap<>();

    /**
     * deviceCode vs productCode vs ThingsProperties
     */
    protected static final Table<String, String, ThingsProperty> DEVICE_PROPERTY_MAP = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);


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
    protected static final Table<String, String, Set<ThingsInterceptors>> THINGS_INTERCEPTORS_TABLE = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);


    /**
     * identifier vs productCode vs ThingsHandlers
     */
    protected static final Table<String, String, Set<ThingsHandlers>> THINGS_HANDLERS_TABLE = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);

    public static <T> T getThingsBean(Class<T> requiredType) {
        for (Map.Entry<String, ThingsContainer> entry : THINGS_CONTAINERS.entrySet()) {
            try {
                T bean = entry.getValue().getBean(requiredType);
                if (bean != null) {
                    return bean;
                }
            } catch (Exception ignored) {
            }
        }
        log.error("Not found Things Bean : [{}]", requiredType);
        return null;
    }

}
