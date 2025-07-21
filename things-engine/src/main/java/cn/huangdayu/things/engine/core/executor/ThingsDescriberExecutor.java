package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.container.ThingsContainer;
import cn.huangdayu.things.api.container.ThingsDescriber;
import cn.huangdayu.things.common.annotation.*;
import cn.huangdayu.things.common.dsl.*;
import cn.huangdayu.things.common.events.ThingsContainerCancelledEvent;
import cn.huangdayu.things.common.events.ThingsContainerRegisteredEvent;
import cn.huangdayu.things.common.message.AbstractThingsMessage;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.engine.wrapper.ThingsEventEntities;
import cn.huangdayu.things.engine.wrapper.ThingsFunction;
import cn.huangdayu.things.engine.wrapper.ThingsParameter;
import cn.huangdayu.things.engine.wrapper.ThingsPropertyEntities;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.huangdayu.things.common.utils.ThingsUtils.*;

/**
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
@ThingsBean
public class ThingsDescriberExecutor implements ThingsDescriber {

    private static final String CACHE_KEY = "things_dsl_cache";
    private final ThingsEventObserver thingsEventObserver;
    private final ThingsContainerManager thingsContainerManager;
    private final Cache<String, ThingsDslInfo> CACHE = CacheUtil.newTimedCache(TimeUnit.MINUTES.toMillis(10L));

    @PostConstruct
    public void init() {
        thingsEventObserver.registerObserver(ThingsContainerRegisteredEvent.class, engineEvent -> CACHE.remove(CACHE_KEY));
        thingsEventObserver.registerObserver(ThingsContainerCancelledEvent.class, engineEvent -> CACHE.remove(CACHE_KEY));
    }

    @Override
    public ThingsDslInfo getDSL() {
        return CACHE.get(CACHE_KEY, () -> getDslInfo(null));
    }

    @Override
    public ThingsDslInfo getDSL(ThingsContainer thingsContainer) {
        return getDslInfo(thingsContainer);
    }

    private ThingsDslInfo getDslInfo(ThingsContainer thingsContainer) {
        return new ThingsDslInfo(getDomainInfo(thingsContainer), getThingsInfo(thingsContainer));
    }

    private Set<DomainInfo> getDomainInfo(ThingsContainer thingsContainer) {
        Set<DomainInfo> domainDsl = new HashSet<>();
        DomainInfo domainInfo = new DomainInfo();
        domainInfo.setSubscribes(getSubscribes(thingsContainer));
        domainInfo.setConsumes(getConsumes(thingsContainer));
        domainInfo.setProfile(getDomainProfile());
        domainDsl.add(domainInfo);
        return domainDsl;
    }

    private DomainProfileInfo getDomainProfile() {
        DomainProfileInfo domainProfileInfo = new DomainProfileInfo();
        domainProfileInfo.setCode(getUUID());
        domainProfileInfo.setName("ThingsDomain");
        domainProfileInfo.setSchema("1.0");
        return domainProfileInfo;
    }

    private Set<DomainConsumeInfo> getConsumes(ThingsContainer thingsContainer) {
        return thingsContainerManager.getThingsClientTable().cellSet().stream()
                .filter(v -> thingsContainer == null || v.getValue().getThingsContainer() == thingsContainer)
                .map(cell -> new DomainConsumeInfo(cell.getColumnKey(), cell.getRowKey())).collect(Collectors.toSet());
    }

    private Set<DomainSubscribeInfo> getSubscribes(ThingsContainer thingsContainer) {
        return thingsContainerManager.getThingsEventsListenerTable().cellSet().stream()
                .filter(v -> v.getValue().stream().anyMatch(w -> thingsContainer == null || w.getThingsContainer() == thingsContainer))
                .map(cell -> new DomainSubscribeInfo(cell.getColumnKey(), cell.getRowKey())).collect(Collectors.toSet());
    }

    private Set<ThingsTemplate> getThingsInfo(ThingsContainer thingsContainer) {
        return thingsContainerManager.getThingsEntityTable().cellSet().stream()
                .filter(v -> thingsContainer == null || v.getValue().getThingsContainer() == thingsContainer)
                .map(m -> getThingsInfo(m.getRowKey())).collect(Collectors.toSet());
    }

    private ThingsTemplate getThingsInfo(String productCode) {
        ThingsTemplate thingsTemplate = initThingsInfo(productCode);
        thingsTemplate.getActions().addAll(getServices(productCode));
        thingsTemplate.getEvents().addAll(getEvents(productCode));
        thingsTemplate.getProperties().addAll(getProperties(productCode));
        return thingsTemplate;
    }

    private ThingsTemplate initThingsInfo(String productCode) {
        ThingsTemplate thingsTemplate = new ThingsTemplate();
        thingsTemplate.setEvents(new ConcurrentHashSet<>());
        thingsTemplate.setActions(new ConcurrentHashSet<>());
        thingsTemplate.setProperties(new ConcurrentHashSet<>());
        thingsTemplate.setProfile(getThingsProfile(productCode));
        return thingsTemplate;
    }

    private ThingsEntity getThings(String productCode) {
        return thingsContainerManager.getThingsEntityTable().getRow(productCode).values().stream().findFirst().orElseThrow().getThingsEntity();
    }

    private ThingsProfileInfo getThingsProfile(String productCode) {
        ThingsEntity thingsEntity = getThings(productCode);
        ThingsProfileInfo productInfo = new ThingsProfileInfo();
        productInfo.setProductCode(productCode);
        productInfo.setDescription(thingsEntity.desc());
        productInfo.setProductClass(thingsEntity.productType());
        productInfo.setVersion(thingsEntity.version());
        return productInfo;
    }

    private Set<ThingsParamInfo> getProperties(String productCode) {
        Set<ThingsParamInfo> params = new ConcurrentHashSet<>();
        ThingsPropertyEntities thingsPropertyEntities = thingsContainerManager.getThingsPropertyMap().get(productCode);
        if (thingsPropertyEntities != null) {
            params.addAll(getParams(thingsPropertyEntities.getBean().getClass(), true));
        }
        return params;
    }

    private Set<ThingsEventInfo> getEvents(String productCode) {
        Map<String, ThingsEventEntities> map = thingsContainerManager.getThingsEventsTable().getColumn(productCode);
        return map.values().stream().map(thingsEventEntities -> {
            ThingsEventInfo events = copyAnnotationValues(thingsEventEntities.getThingsEventEntity(), new ThingsEventInfo());
            events.setOutput(getParams(thingsEventEntities.getBean().getClass()));
            return events;
        }).collect(Collectors.toSet());
    }

    private Set<ThingsActionInfo> getServices(String productCode) {
        return thingsContainerManager.getThingsFunctionTable().getColumn(productCode).entrySet().parallelStream()
                .filter(entry -> entry.getValue().getMethodAnnotation() instanceof ThingsService)
                .map(entry -> getServices(entry.getKey(), entry.getValue(), (ThingsService) entry.getValue().getMethodAnnotation()))
                .collect(Collectors.toSet());
    }

    private ThingsActionInfo getServices(String identifier, ThingsFunction thingsFunction, ThingsService thingsService) {
        ThingsActionInfo services = new ThingsActionInfo();
        services.setIdentifier(identifier);
        services.setName(thingsService.name());
        services.setDescription(thingsService.desc());
        services.setCallType(thingsService.async() ? "async" : "sync");
        services.setInput(getInputParams(thingsFunction));
        services.setOutput(getOutputParams(thingsFunction));
        return services;
    }

    private Set<ThingsParamInfo> getInputParams(ThingsFunction thingsFunction) {
        Set<ThingsParamInfo> params = new ConcurrentHashSet<>();
        if (thingsFunction.getThingsParameters() != null) {
            for (ThingsParameter thingsParameter : thingsFunction.getThingsParameters()) {
                if (thingsParameter == null) {
                    continue;
                }
                if (thingsParameter.getAnnotation() instanceof ThingsParams) {
                    params.addAll(getParams(ReflectUtil.getFields(thingsParameter.getType())));
                } else if (thingsParameter.getAnnotation() instanceof ThingsMessage) {
                    if (thingsParameter.getType().isAssignableFrom(AbstractThingsMessage.class)) {
                        params.addAll(getParams(thingsFunction, thingsParameter.getIndex(), 1));
                    }
                } else if (thingsParameter.getAnnotation() instanceof ThingsParam thingsParam) {
                    params.add(getParam(thingsParam, thingsParameter));
                }
            }
        }
        return params;
    }

    private Set<ThingsParamInfo> getOutputParams(ThingsFunction thingsFunction) {
        Class<?> returnType = thingsFunction.getMethod().getReturnType();
        return getParams(returnType, filterJSONObject());
    }

    private Filter<Field> filterJSONObject() {
        return field -> !field.getDeclaringClass().isAssignableFrom(JSONObject.class);
    }

    private Set<ThingsParamInfo> getParams(ThingsFunction thingsFunction, int parameterIndex, int typeIndex, Filter<Field> fieldFilter) {
        return getParams(getParameterType(thingsFunction.getMethod(), parameterIndex, typeIndex), fieldFilter);
    }

    private Set<ThingsParamInfo> getParams(ThingsFunction thingsFunction, int parameterIndex, int typeIndex) {
        return getParams(getParameterType(thingsFunction.getMethod(), parameterIndex, typeIndex));
    }

    private Set<ThingsParamInfo> getParams(Type type) {
        return getParams(ReflectUtil.getFields(TypeUtil.getClass(type)));
    }

    private Set<ThingsParamInfo> getParams(Type type, Filter<Field> fieldFilter) {
        return getParams(ReflectUtil.getFields(TypeUtil.getClass(type), fieldFilter));
    }

    private Set<ThingsParamInfo> getParams(Class<?> beanClass, Filter<Field> fieldFilter) {
        return getParams(ReflectUtil.getFields(beanClass, fieldFilter));
    }

    private Set<ThingsParamInfo> getParams(Class<?> beanClass) {
        return getParams(ReflectUtil.getFields(beanClass));
    }

    private Set<ThingsParamInfo> getParams(Class<?> beanClass, boolean setAccessMode) {
        return getParams(ReflectUtil.getFields(beanClass), setAccessMode);
    }

    private Set<ThingsParamInfo> getParams(Field[] fields) {
        return getParams(fields, false);
    }

    private Set<ThingsParamInfo> getParams(Field[] fields, boolean setAccessMode) {
        Set<ThingsParamInfo> params = new ConcurrentHashSet<>();
        for (Field field : fields) {
            ThingsParamInfo param = getParam(field, setAccessMode);
            params.add(param);
            try {
                switch (param.getDataType()) {
                    case "struct" -> params.addAll(getParams(param.getIdentifier(), field, setAccessMode));
                    case "array" -> {
                        String type = convertDataTypeName(field.getType());
                        param.setChildDataType(type);
                        if (type.equals("struct")) {
                            params.addAll(getParams(param.getIdentifier(), field, setAccessMode));
                        }
                    }
                    case "enum" -> param.setEnumValue(convertEnumToNames(field));
                }
            } catch (Exception e) {
                log.error("Things convert param {} {} type exception", param.getIdentifier(), param, e);
            }
        }
        return params;
    }

    private Set<ThingsParamInfo> getParams(String structIdentifier, Field field, boolean setAccessMode) {
        Set<ThingsParamInfo> params = getParams(ReflectUtil.getFields(field.getType()), setAccessMode);
        params.forEach(v -> v.setIdentifier(structIdentifier + "." + v.getIdentifier()));
        return params;
    }

    private ThingsParamInfo getParam(Field field) {
        return getParam(field.getAnnotation(ThingsParam.class), field, false);
    }

    private ThingsParamInfo getParam(Field field, boolean setAccessMode) {
        return getParam(field.getAnnotation(ThingsParam.class), field, setAccessMode);
    }

    private ThingsParamInfo getParam(ThingsParam thingsParam, Field field, boolean setAccessMode) {
        return getParam(thingsParam, field.getType(), field.getName(), setAccessMode);
    }

    private ThingsParamInfo getParam(ThingsParam thingsParam, ThingsParameter thingsParameter) {
        return getParam(thingsParam, thingsParameter.getType(), thingsParameter.getName(), false);
    }

    private ThingsParamInfo getParam(ThingsParam thingsParam, Class<?> clazz, String name, boolean setAccessMode) {
        ThingsParamInfo param = new ThingsParamInfo();
        if (thingsParam != null) {
            param.setRequired(thingsParam.required());
            copyAnnotationValues(thingsParam, param);
            param.setDescription(thingsParam.desc());
            param.setName(thingsParam.name());
            if (setAccessMode) {
                param.setAccessMode(thingsParam.accessMode());
            }
        }
        param.setDataType(convertDataTypeName(clazz));
        param.setIdentifier(name);
        return param;
    }


    private String convertEnumToNames(Field field) {
        final Enum<?>[] enums = (Enum<?>[]) field.getType().getEnumConstants();
        if (null != enums) {
            final List<String> list = new ArrayList<>(enums.length);
            for (Enum<?> e : enums) {
                list.add(e.name());
            }
            return CollUtil.join(list, ",");
        }
        return null;
    }
}
