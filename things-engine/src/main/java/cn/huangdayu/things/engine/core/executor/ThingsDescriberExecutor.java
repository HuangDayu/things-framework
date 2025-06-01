package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.container.ThingsDescriber;
import cn.huangdayu.things.api.infrastructure.ThingsConfigurator;
import cn.huangdayu.things.common.annotation.*;
import cn.huangdayu.things.common.dsl.*;
import cn.huangdayu.things.common.message.BaseThingsMessage;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.observer.event.ThingsContainerUpdatedEvent;
import cn.huangdayu.things.engine.wrapper.ThingsEvents;
import cn.huangdayu.things.engine.wrapper.ThingsFunction;
import cn.huangdayu.things.engine.wrapper.ThingsParameter;
import cn.huangdayu.things.engine.wrapper.ThingsProperty;
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
public class ThingsDescriberExecutor extends ThingsBaseExecutor implements ThingsDescriber {

    private static final String CACHE_KEY = "things_dsl_cache";
    private final ThingsEventObserver thingsEventObserver;
    private final ThingsConfigurator thingsConfigService;
    private final Cache<String, ThingsDslInfo> CACHE = CacheUtil.newTimedCache(TimeUnit.MINUTES.toMillis(10L));

    @PostConstruct
    public void init() {
        thingsEventObserver.registerObserver(ThingsContainerUpdatedEvent.class, engineEvent -> CACHE.remove(CACHE_KEY));
    }

    @Override
    public ThingsDslInfo getDsl() {
        return CACHE.get(CACHE_KEY, this::getDslInfo);
    }

    private ThingsDslInfo getDslInfo() {
        return new ThingsDslInfo(getDomainInfo(), getThingsInfo());
    }

    private Set<DomainInfo> getDomainInfo() {
        Set<DomainInfo> domainDsl = new HashSet<>();
        DomainInfo domainInfo = new DomainInfo();
        domainInfo.setSubscribes(getSubscribes());
        domainInfo.setConsumes(getConsumes());
        domainInfo.setProfile(getDomainProfile());
        domainDsl.add(domainInfo);
        return domainDsl;
    }

    private DomainProfile getDomainProfile() {
        DomainProfileInfo domainProfileInfo = new DomainProfileInfo();
        domainProfileInfo.setCode(getUUID());
        domainProfileInfo.setName("ThingsDomain");
        DomainProfile profile = new DomainProfile();
        profile.setSchema("1.0");
        return profile;
    }

    private Set<DomainConsumeInfo> getConsumes() {
        return ThingsBaseExecutor.THINGS_CLIENT_TABLE.cellSet()
                .stream().map(cell -> new DomainConsumeInfo(cell.getColumnKey(), cell.getRowKey())).collect(Collectors.toSet());
    }

    private Set<DomainSubscribeInfo> getSubscribes() {
        return ThingsBaseExecutor.THINGS_EVENTS_LISTENER_TABLE.cellSet()
                .stream().map(cell -> new DomainSubscribeInfo(cell.getColumnKey(), cell.getRowKey())).collect(Collectors.toSet());
    }

    private Set<ThingsInfo> getThingsInfo() {
        return THINGS_ENTITY_TABLE.rowKeySet().parallelStream().map(this::getThingsInfo).collect(Collectors.toSet());
    }

    private ThingsInfo getThingsInfo(String productCode) {
        ThingsInfo thingsInfo = initThingsInfo(productCode);
        thingsInfo.getServices().addAll(getServices(productCode));
        thingsInfo.getEvents().addAll(getEvents(productCode));
        thingsInfo.getProperties().addAll(getProperties(productCode));
        return thingsInfo;
    }

    private ThingsInfo initThingsInfo(String productCode) {
        ThingsInfo thingsInfo = new ThingsInfo();
        thingsInfo.setEvents(new ConcurrentHashSet<>());
        thingsInfo.setServices(new ConcurrentHashSet<>());
        thingsInfo.setProperties(new ConcurrentHashSet<>());
        thingsInfo.setProfile(getThingsProfile(productCode));
        return thingsInfo;
    }

    private Things getThings(String productCode) {
        return THINGS_ENTITY_TABLE.getRow(productCode).values().stream().findFirst().orElseThrow().getThings();
    }

    private ThingsProfile getThingsProfile(String productCode) {
        Things things = getThings(productCode);
        ThingsProfileInfo productInfo = new ThingsProfileInfo();
        productInfo.setCode(productCode);
        productInfo.setName(things.name());
        ThingsProfile profile = new ThingsProfile();
        profile.setProduct(productInfo);
        profile.setSchema(things.schema());
        return profile;
    }

    private Set<ThingsParamInfo> getProperties(String productCode) {
        Set<ThingsParamInfo> params = new ConcurrentHashSet<>();
        ThingsProperty thingsProperty = PRODUCT_PROPERTY_MAP.get(productCode);
        if (thingsProperty != null) {
            params.addAll(getParams(thingsProperty.getBean().getClass(), true));
        }
        return params;
    }

    private Set<ThingsEventInfo> getEvents(String productCode) {
        Map<String, ThingsEvents> map = THINGS_EVENTS_TABLE.getColumn(productCode);
        return map.values().stream().map(thingsEvents -> {
            ThingsEventInfo events = copyAnnotationValues(thingsEvents.getThingsEventEntity(), new ThingsEventInfo());
            events.setOutputData(getParams(thingsEvents.getBean().getClass()));
            return events;
        }).collect(Collectors.toSet());
    }

    private Set<ThingsServiceInfo> getServices(String productCode) {
        return THINGS_SERVICES_TABLE.getColumn(productCode).entrySet().parallelStream().filter(entry -> entry.getValue().getMethodAnnotation() instanceof ThingsService).map(entry -> getServices(entry.getKey(), entry.getValue(), (ThingsService) entry.getValue().getMethodAnnotation())).collect(Collectors.toSet());
    }

    private ThingsServiceInfo getServices(String identifier, ThingsFunction thingsFunction, ThingsService thingsService) {
        ThingsServiceInfo services = new ThingsServiceInfo();
        services.setIdentifier(identifier);
        services.setName(thingsService.name());
        services.setDesc(thingsService.desc());
        services.setCallType(thingsService.async() ? "async" : "sync");
        services.setInputData(getInputParams(thingsFunction));
        services.setOutputData(getOutputParams(thingsFunction));
        return services;
    }

    private Set<ThingsParamInfo> getInputParams(ThingsFunction thingsFunction) {
        Set<ThingsParamInfo> params = new ConcurrentHashSet<>();
        if (thingsFunction.getThingsParameters() != null) {
            for (ThingsParameter thingsParameter : thingsFunction.getThingsParameters()) {
                if (thingsParameter == null) {
                    continue;
                }
                if (thingsParameter.getAnnotation() instanceof ThingsPayload) {
                    params.addAll(getParams(ReflectUtil.getFields(thingsParameter.getType())));
                } else if (thingsParameter.getAnnotation() instanceof ThingsMessage) {
                    if (thingsParameter.getType().isAssignableFrom(BaseThingsMessage.class)) {
                        params.addAll(getParams(thingsFunction, thingsParameter.getIndex(), 1));
                    }
                } else if (thingsParameter.getAnnotation() instanceof ThingsParam thingsParam) {
                    if (thingsParam.bodyType().equals(ThingsParam.BodyType.PAYLOAD)) {
                        params.add(getParam(thingsParam, thingsParameter));
                    }
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
            ThingsDataType dataType = param.getDataType();
            try {
                switch (dataType.getType()) {
                    case "struct" -> params.addAll(getParams(param.getIdentifier(), field, setAccessMode));
                    case "array" -> {
                        String type = convertDataTypeName(field.getType());
                        dataType.setArrayType(type);
                        if (type.equals("struct")) {
                            params.addAll(getParams(param.getIdentifier(), field, setAccessMode));
                        }
                    }
                    case "enum" -> dataType.setEnumNames(convertEnumToNames(field));
                }
            } catch (Exception e) {
                log.error("Things convert param {} {} type exception", param.getIdentifier(), dataType.getType(), e);
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
        ThingsDataType dataType = new ThingsDataType();
        if (thingsParam != null) {
            dataType.setRequired(thingsParam.required());
            dataType.setSpecs(copyAnnotationValues(thingsParam, new ThingsSpecs()));
            param.setDescription(thingsParam.desc());
            param.setName(thingsParam.name());
            if (setAccessMode) {
                param.setAccessMode(thingsParam.accessMode());
            }
        }
        dataType.setType(convertDataTypeName(clazz));
        param.setIdentifier(name);
        param.setDataType(dataType);
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
