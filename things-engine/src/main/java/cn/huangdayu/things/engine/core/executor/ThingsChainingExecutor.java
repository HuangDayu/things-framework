package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.message.ThingsFiltering;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.ThingsChainingType;
import cn.huangdayu.things.common.enums.ThingsMethodType;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.engine.wrapper.ThingsFilters;
import cn.huangdayu.things.engine.wrapper.ThingsHandlers;
import cn.huangdayu.things.engine.wrapper.ThingsInterceptors;
import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.LRUCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.multi.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_SEPARATOR;
import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_WILDCARD;
import static cn.huangdayu.things.common.enums.ThingsChainingType.INPUTTING;
import static cn.huangdayu.things.common.enums.ThingsChainingType.OUTPUTTING;
import static cn.huangdayu.things.common.enums.ThingsMethodType.ALL_METHOD;
import static cn.huangdayu.things.common.utils.ThingsUtils.subIdentifies;
import static cn.huangdayu.things.engine.core.executor.ThingsBaseExecutor.*;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class ThingsChainingExecutor implements ThingsChaining {

    /**
     * LRU (least recently used) 最近最久未使用缓存
     * 根据使用时间来判定对象是否被持续缓存
     */
    private static final Cache<String, ChainingValues> CHAINING_VALUES_CACHE = new LRUCache<>(1000, TimeUnit.MINUTES.toMillis(10));

    @Override
    public boolean input(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        return doChain(thingsRequest, thingsResponse, INPUTTING);
    }

    @Override
    public boolean output(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        return doChain(thingsRequest, thingsResponse, OUTPUTTING);
    }

    /**
     * 输入和输出的消息都经过【过滤，拦截，处理，拦截】
     */
    private boolean doChain(ThingsRequest thingsRequest, ThingsResponse thingsResponse, ThingsChainingType chainingType) {
        ChainingValues chainingValues = getChainingValues(thingsRequest, thingsResponse, chainingType);
        // 获取拦截器链
        Set<ThingsInterceptors> interceptors = chainingValues.getThingsInterceptors();
        Exception exception = null;
        try {
            // 前置拦截器
            if (!interceptorPreHandle(chainingType, thingsRequest, thingsResponse, interceptors)) {
                return false;
            }
            // 遍历执行所有处理器
            chainingValues.getThingsHandlers().forEach(handlers -> handlers.getThingsHandling().doHandle(thingsRequest, thingsResponse));
            // 后置拦截器
            interceptorPostHandle(thingsRequest, thingsResponse, interceptors);
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            // 完成拦截器
            interceptorAfterCompletion(thingsRequest, thingsResponse, exception, interceptors);
        }
        return true;
    }

    private ChainingValues getChainingValues(ThingsRequest thingsRequest, ThingsResponse thingsResponse, ThingsChainingType chainingType) {
        ChainingKeys keys = getKeys(thingsRequest.getJtm(), chainingType);
        return CHAINING_VALUES_CACHE.get(keys.getKeyFlag(), () -> {
            Set<ThingsFilters> filter = filter(keys, thingsRequest, chainingType);
            Set<ThingsInterceptors> interceptors = getInterceptors(keys, thingsRequest, chainingType);
            Set<ThingsHandlers> handlers = getHandlers(keys, thingsRequest, thingsResponse, chainingType);
            return new ChainingValues(filter, handlers, interceptors);
        });
    }

    private boolean interceptorPreHandle(ThingsChainingType chainingType, ThingsRequest thingsRequest, ThingsResponse thingsResponse, Set<ThingsInterceptors> interceptors) {
        for (ThingsInterceptors interceptor : interceptors) {
            if (!interceptor.getThingsIntercepting().preHandle(thingsRequest, thingsResponse)) {
                return false;
            }
        }
        return true;
    }

    private void doFilterChain(ThingsRequest thingsRequest, ThingsResponse thingsResponse, Set<ThingsFilters> filters) {
        if (CollUtil.isNotEmpty(filters)) {
            ThingsFiltering.Chain chain = new ThingsFiltering.Chain(filters.stream().map(ThingsFilters::getThingsFiltering).collect(Collectors.toList()));
            chain.doFilter(thingsRequest, thingsResponse);
        }
    }

    private void interceptorPostHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse, Set<ThingsInterceptors> interceptors) {
        for (ThingsInterceptors interceptor : interceptors) {
            interceptor.getThingsIntercepting().postHandle(thingsRequest, thingsResponse);
        }
    }


    private void interceptorAfterCompletion(ThingsRequest thingsRequest, ThingsResponse thingsResponse, Exception exception, Set<ThingsInterceptors> interceptors) {
        for (ThingsInterceptors interceptor : interceptors) {
            interceptor.getThingsIntercepting().afterCompletion(thingsRequest, thingsResponse, exception);
        }
    }


    private Set<ThingsHandlers> getHandlers(ChainingKeys chainingKeys, ThingsRequest thingsRequest, ThingsResponse thingsResponse, ThingsChainingType chainingType) {
        return getChains(chainingKeys, THINGS_HANDLERS_TABLE, thingsRequest.getJtm(), i -> i.getThingsHandler().order(),
                v -> v.getChainingType().equals(chainingType) && v.getThingsHandling().canHandle(thingsRequest, thingsResponse));
    }

    private Set<ThingsInterceptors> getInterceptors(ChainingKeys chainingKeys, ThingsRequest thingsRequest, ThingsChainingType chainingType) {
        return getChains(chainingKeys, THINGS_INTERCEPTORS_TABLE, thingsRequest.getJtm(), i -> i.getThingsInterceptor().order(), v -> v.getChainingType().equals(chainingType));
    }

    private Set<ThingsFilters> filter(ChainingKeys chainingKeys, ThingsRequest thingsRequest, ThingsChainingType chainingType) {
        return getChains(chainingKeys, THINGS_FILTERS_TABLE, thingsRequest.getJtm(), i -> i.getThingsFilter().order(), v -> v.getChainingType().equals(chainingType));
    }

    private <T> Set<T> getChains(ChainingKeys chainingKeys, Table<String, String, Set<T>> table, JsonThingsMessage jtm, Function<T, Integer> comparing, Predicate<T> filtering) {
        Set<T> linkedHashSet = new LinkedHashSet<>();
        chainingKeys.getKeys().forEach(key -> {
            Set<T> values = table.get(key.identifier, key.productCode);
            if (CollUtil.isNotEmpty(values)) {
                linkedHashSet.addAll(values);
            }
        });
        Set<T> set = table.get(ALL_METHOD + THINGS_SEPARATOR + THINGS_WILDCARD, THINGS_WILDCARD);
        if (CollUtil.isNotEmpty(set)) {
            linkedHashSet.addAll(set);
        }
        return linkedHashSet.stream().filter(filtering).sorted(Comparator.comparing(comparing)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private ChainingKeys getKeys(JsonThingsMessage jtm, ThingsChainingType chainingType) {
        Set<ChainingKey> keys = new HashSet<>();
        BaseThingsMetadata baseMetadata = jtm.getBaseMetadata();
        ThingsMethodType thingsMethodType = ThingsMethodType.getMethodType(extractMiddlePart(jtm.getMethod()));
        String identifies = subIdentifies(jtm.getMethod());
        keys.add(new ChainingKey(thingsMethodType + THINGS_SEPARATOR + identifies, THINGS_WILDCARD));
        keys.add(new ChainingKey(thingsMethodType + THINGS_SEPARATOR + identifies, baseMetadata.getProductCode()));
        keys.add(new ChainingKey(thingsMethodType + THINGS_SEPARATOR + THINGS_WILDCARD, baseMetadata.getProductCode()));
        keys.add(new ChainingKey(thingsMethodType + THINGS_SEPARATOR + THINGS_WILDCARD, THINGS_WILDCARD));
        return new ChainingKeys(keys, chainingType + THINGS_SEPARATOR + thingsMethodType + THINGS_SEPARATOR + identifies + THINGS_SEPARATOR + baseMetadata.getProductCode());
    }

    private static String extractMiddlePart(String input) {
        String[] parts = input.split("\\.");
        if (parts.length >= 1) {
            return parts[1];
        }
        return ThingsMethodType.ALL_METHOD.name();
    }

    @Data
    @AllArgsConstructor
    private static class ChainingKey {
        private final String identifier;
        private final String productCode;
    }

    @Data
    @AllArgsConstructor
    private static class ChainingValues {
        private final Set<ThingsFilters> thingsFilters;
        private final Set<ThingsHandlers> thingsHandlers;
        private final Set<ThingsInterceptors> thingsInterceptors;
    }

    @Data
    @AllArgsConstructor
    private static class ChainingKeys {
        private final Set<ChainingKey> keys;
        private final String keyFlag;
    }
}
