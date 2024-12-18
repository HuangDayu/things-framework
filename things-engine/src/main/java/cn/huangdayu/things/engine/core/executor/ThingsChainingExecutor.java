package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.message.ThingsFiltering;
import cn.huangdayu.things.api.message.ThingsHandling;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.ThingsMethodType;
import cn.huangdayu.things.common.enums.ThingsStreamingType;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.engine.wrapper.ThingsFilters;
import cn.huangdayu.things.engine.wrapper.ThingsHandlers;
import cn.huangdayu.things.engine.wrapper.ThingsInterceptors;
import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.multi.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.BAD_REQUEST;
import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_SEPARATOR;
import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_WILDCARD;
import static cn.huangdayu.things.common.enums.ThingsStreamingType.INPUTTING;
import static cn.huangdayu.things.common.enums.ThingsStreamingType.OUTPUTTING;
import static cn.huangdayu.things.common.utils.ThingsUtils.subIdentifies;
import static cn.huangdayu.things.engine.core.executor.ThingsBaseExecutor.*;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsChainingExecutor implements ThingsChaining {

    /**
     * 将查询结果缓存，减少重复查询
     */
    private static final Cache<String, ChainingValues> CACHE_VALUES = new TimedCache<>(TimeUnit.MINUTES.toMillis(10));

    @Override
    public void input(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        doChain(thingsRequest, thingsResponse, INPUTTING);
    }

    @Override
    public void output(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        doChain(thingsRequest, thingsResponse, OUTPUTTING);
    }

    /**
     * 输入和输出的消息都经过【过滤，拦截，处理，拦截】
     */
    protected void doChain(ThingsRequest thingsRequest, ThingsResponse thingsResponse, ThingsStreamingType sourceType) {
        ChainingValues chainingValues = getCacheValues(thingsRequest, sourceType);
        // 执行过滤器链
        Set<ThingsFilters> filters = chainingValues.getThingsFilters();
        if (CollUtil.isNotEmpty(filters)) {
            ThingsFiltering.Chain chain = new ThingsFiltering.Chain(filters.stream().map(ThingsFilters::getThingsFiltering).collect(Collectors.toList()));
            chain.doFilter(thingsRequest, thingsResponse);
        }
        // 获取拦截器链
        Set<ThingsInterceptors> interceptors = chainingValues.getThingsInterceptors();
        // 获取处理器链
        Set<ThingsHandlers> thingsHandlers = chainingValues.getThingsHandlers();
        for (ThingsHandlers handlers : thingsHandlers) {
            ThingsHandling thingsHandling = handlers.getThingsHandling();
            Exception exception = null;
            try {
                // 前置拦截器
                interceptorPreHandle(thingsRequest, thingsResponse, thingsHandling, interceptors);
                thingsHandling.doHandle(thingsRequest, thingsResponse);
                // 后置拦截器
                interceptorPostHandle(thingsRequest, thingsResponse, thingsHandling, interceptors);
            } catch (Exception e) {
                exception = e;
                throw e;
            } finally {
                // 完成拦截器
                interceptorAfterCompletion(thingsRequest, thingsResponse, thingsHandling, exception, interceptors);
            }
        }
    }

    private ChainingValues getCacheValues(ThingsRequest thingsRequest, ThingsStreamingType sourceType) {
        ChainingKeys keys = getKeys(thingsRequest.getJtm(), sourceType);
        return CACHE_VALUES.get(keys.getKeyFlag(), () -> {
            Set<ThingsFilters> filter = filter(keys, thingsRequest, sourceType);
            Set<ThingsInterceptors> interceptors = getInterceptors(keys, thingsRequest, sourceType);
            Set<ThingsHandlers> handlers = getHandlers(keys, thingsRequest, sourceType);
            return new ChainingValues(filter, handlers, interceptors);
        });
    }

    protected void interceptorPreHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse, ThingsHandling thingsHandling, Set<ThingsInterceptors> interceptors) {
        for (ThingsInterceptors interceptor : interceptors) {
            if (!interceptor.getThingsIntercepting().preHandle(thingsRequest, thingsResponse, thingsHandling)) {
                throw new ThingsException(thingsRequest.getJtm(), BAD_REQUEST, "Things interceptor no passing.");
            }
        }
    }

    protected void interceptorPostHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse, ThingsHandling thingsHandling, Set<ThingsInterceptors> interceptors) {
        for (ThingsInterceptors interceptor : interceptors) {
            interceptor.getThingsIntercepting().postHandle(thingsRequest, thingsResponse, thingsHandling);
        }
    }


    protected void interceptorAfterCompletion(ThingsRequest thingsRequest, ThingsResponse thingsResponse, ThingsHandling thingsHandling, Exception exception, Set<ThingsInterceptors> interceptors) {
        for (ThingsInterceptors interceptor : interceptors) {
            interceptor.getThingsIntercepting().afterCompletion(thingsRequest, thingsResponse, thingsHandling, exception);
        }
    }


    private Set<ThingsHandlers> getHandlers(ChainingKeys chainingKeys, ThingsRequest thingsRequest, ThingsStreamingType sourceType) {
        return getChains(chainingKeys, THINGS_HANDLERS_TABLE, thingsRequest.getJtm(), i -> i.getThingsHandler().order(), v -> v.getSourceType().equals(sourceType));
    }

    private Set<ThingsInterceptors> getInterceptors(ChainingKeys chainingKeys, ThingsRequest thingsRequest, ThingsStreamingType sourceType) {
        return getChains(chainingKeys, THINGS_INTERCEPTORS_TABLE, thingsRequest.getJtm(), i -> i.getThingsInterceptor().order(), v -> v.getSourceType().equals(sourceType));
    }

    private Set<ThingsFilters> filter(ChainingKeys chainingKeys, ThingsRequest thingsRequest, ThingsStreamingType sourceType) {
        return getChains(chainingKeys, THINGS_FILTERS_TABLE, thingsRequest.getJtm(), i -> i.getThingsFilter().order(), v -> v.getSourceType().equals(sourceType));
    }

    private <T> Set<T> getChains(ChainingKeys chainingKeys, Table<String, String, Set<T>> table, JsonThingsMessage jtm, Function<T, Integer> comparing, Predicate<T> filtering) {
        Set<T> linkedHashSet = new LinkedHashSet<>();
        chainingKeys.getKeys().forEach(key -> {
            Set<T> values = table.get(key.identifier, key.productCode);
            if (CollUtil.isNotEmpty(values)) {
                linkedHashSet.addAll(values);
            }
        });
        return linkedHashSet.stream().filter(filtering).sorted(Comparator.comparing(comparing)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private ChainingKeys getKeys(JsonThingsMessage jtm, ThingsStreamingType sourceType) {
        Set<ChainingKey> keys = new HashSet<>();
        BaseThingsMetadata baseMetadata = jtm.getBaseMetadata();
        ThingsMethodType thingsMethodType = ThingsMethodType.getMethodType(extractMiddlePart(jtm.getMethod()));
        String identifies = subIdentifies(jtm.getMethod());
        keys.add(new ChainingKey(thingsMethodType + THINGS_SEPARATOR + identifies, THINGS_WILDCARD));
        keys.add(new ChainingKey(thingsMethodType + THINGS_SEPARATOR + identifies, baseMetadata.getProductCode()));
        keys.add(new ChainingKey(thingsMethodType + THINGS_SEPARATOR + THINGS_WILDCARD, baseMetadata.getProductCode()));
        keys.add(new ChainingKey(thingsMethodType + THINGS_SEPARATOR + THINGS_WILDCARD, THINGS_WILDCARD));
        return new ChainingKeys(keys, sourceType + THINGS_SEPARATOR + thingsMethodType + THINGS_SEPARATOR + identifies + THINGS_SEPARATOR + baseMetadata.getProductCode());
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
