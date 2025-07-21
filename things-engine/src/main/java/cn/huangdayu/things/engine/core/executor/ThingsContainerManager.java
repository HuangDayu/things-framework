package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.container.ThingsContainer;
import cn.huangdayu.things.engine.wrapper.*;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author huangdayu
 */
@Slf4j
@Getter
abstract class ThingsContainerManager {
    /**
     * containerName vs ThingsContainer
     */
    protected final Set<ThingsContainer> thingsContainers = new CopyOnWriteArraySet<>();


    /**
     * functionBean vs ThingsContainer
     */
    protected final Map<Object, ThingsContainer> thingsFunctionMap = new ConcurrentHashMap<>();


    /**
     * productCode vs ThingsBeanClass vs ThingsEntity
     */
    protected final Table<String, Class<?>, ThingsEntities> thingsEntityTable = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);


    /**
     * identifier vs productCode vs ThingsFunction
     */
    protected final Table<String, String, ThingsFunction> thingsFunctionTable = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);

    /**
     * productCode vs ThingsProperties
     */
    protected final Map<String, ThingsPropertyEntities> thingsPropertyMap = new ConcurrentHashMap<>();

    /**
     * deviceCode vs productCode vs ThingsProperties
     */
    protected final Table<String, String, ThingsPropertyEntities> devicePropertyMap = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);


    /**
     * identifier vs productCode vs ThingsEvents
     */
    protected final Table<String, String, ThingsEventEntities> thingsEventsTable = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);


    /**
     * identifier vs productCode vs ThingsEventListener
     */
    protected final Table<String, String, Set<ThingsFunction>> thingsEventsListenerTable = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);


    /**
     * identifier vs productCode vs ThingsPropertyListener
     */
    protected final Table<String, String, Set<ThingsFunction>> thingsPropertyListenerTable = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);

    /**
     * identifier vs productCode vs ThingsInterceptors
     */
    protected final Table<String, String, Set<ThingsInterceptors>> thingsInterceptorsTable = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);


    /**
     * identifier vs productCode vs ThingsHandlers
     */
    protected final Table<String, String, Set<ThingsHandlers>> thingsHandlersTable = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);


    /**
     * identifier vs productCode vs ThingsFunction
     */
    protected final Table<String, String, ThingsFunction> thingsClientTable = new RowKeyTable<>(new ConcurrentHashMap<>(), ConcurrentHashMap::new);

    public <T> T getThingsBean(Class<T> requiredType) {
        for (ThingsContainer thingsContainer : thingsContainers) {
            try {
                T bean = thingsContainer.getBean(requiredType);
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
