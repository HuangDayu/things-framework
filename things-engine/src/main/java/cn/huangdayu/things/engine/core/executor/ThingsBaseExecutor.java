package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.api.container.ThingsContainer;
import cn.huangdayu.things.engine.wrapper.*;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.ERROR;

/**
 * @author huangdayu
 */
@Slf4j
public abstract class ThingsBaseExecutor {
    /**
     * containerName vs ThingsContainer
     */
    protected final static Map<String, ThingsContainer> THINGS_CONTAINERS = new ConcurrentHashMap<>();


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

    public static <T> T getThingsBean(Class<T> requiredType) {
        for (Map.Entry<String, ThingsContainer> entry : THINGS_CONTAINERS.entrySet()) {
            try {
                return entry.getValue().getBean(requiredType);
            } catch (Exception ignored) {
            }
        }
        throw new ThingsException(null, ERROR, "Not found Things Bean .");
    }

}
