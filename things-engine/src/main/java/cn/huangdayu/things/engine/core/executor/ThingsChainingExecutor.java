package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.ThingsChainingType;
import cn.huangdayu.things.common.enums.ThingsMethodType;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.ThingsMessageMethod;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.engine.wrapper.ThingsHandlers;
import cn.huangdayu.things.engine.wrapper.ThingsInterceptors;
import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.cache.impl.LRUCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.multi.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.ERROR;
import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_SEPARATOR;
import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_WILDCARD;
import static cn.huangdayu.things.common.enums.ThingsChainingType.INPUTTING;
import static cn.huangdayu.things.common.enums.ThingsChainingType.OUTPUTTING;
import static cn.huangdayu.things.common.enums.ThingsMethodType.ALL_METHOD;
import static cn.huangdayu.things.common.utils.ThingsUtils.subIdentifies;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
@RequiredArgsConstructor
public class ThingsChainingExecutor implements ThingsChaining {

    private final ThingsContainerManager thingsContainerManager;

    /**
     * LRU (least recently used) 最近最久未使用缓存
     * 根据使用时间来判定对象是否被持续缓存
     */
    private static final Cache<String, ChainingValues> CHAINING_VALUES_CACHE = new LRUCache<>(1000, TimeUnit.MINUTES.toMillis(10));

    /**
     * 已处理的输入消息先进先出缓存，防止重复处理消息，执行失败可以重试
     * <p>
     * messageId vs failed sum
     * 消息id vs 失败次数
     */
    private static final Cache<String, AtomicInteger> INTPUTED_CACHE = new FIFOCache<>(1000);

    /**
     * 已处理的输出消息先进先出缓存，防止重复处理消息，执行失败可以重试
     * messageId vs failed sum
     * 消息id vs 失败次数
     */
    private static final Cache<String, AtomicInteger> OUTPUTED_CACHE = new FIFOCache<>(1000);


    /**
     * 重试次数
     */
    public static final int MAX_FAILED_SUM = 3;


    @Override
    public boolean input(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        if (doChain(thingsRequest, thingsResponse, INPUTTING, INTPUTED_CACHE)) {
            return responseInput(thingsRequest, thingsResponse);
        }
        return false;
    }

    @Override
    public boolean output(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        return doChain(thingsRequest, thingsResponse, OUTPUTTING, OUTPUTED_CACHE);
    }

    /**
     * 输入处理完成后，如果存在响应消息，则进行输入转输出处理
     *
     * @param thingsRequest
     * @param thingsResponse
     * @return
     */
    private boolean responseInput(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        if (thingsResponse.getTrm() != null) {
            thingsResponse.setTarget(thingsRequest.getSource());
            return output(thingsRequest, new ThingsResponse());
        }
        return true;
    }

    /**
     * 输入和输出的消息都经过【过滤，拦截，处理，拦截】
     */
    private boolean doChain(ThingsRequest thingsRequest, ThingsResponse thingsResponse, ThingsChainingType chainingType, Cache<String, AtomicInteger> cache) {
        ChainingValues chainingValues = getChainingValues(thingsRequest, thingsResponse, chainingType);
        ExceptionValue exceptionValue = new ExceptionValue();
        try {
            // 如果已经处理过则不再处理，0 未执行，小于 0 已成功执行，大于 0 失败次数
            AtomicInteger atomicInteger = cache.get(thingsRequest.getTrm().getId(), () -> new AtomicInteger(0));
            if (atomicInteger.get() < 0 || atomicInteger.get() >= MAX_FAILED_SUM) {
                throw new ThingsException(ERROR, "Things chaining repeatedly messages: " + thingsRequest.getTrm().getId());
            }
            // 遍历执行所有前置拦截器
            chainingValues.getThingsInterceptors().forEach(interceptor -> {
                if (!interceptor.getThingsIntercepting().preHandle(thingsRequest, thingsResponse)) {
                    throw new ThingsException(ERROR, "Things chaining " + interceptor.getThingsIntercepting().getClass().getSimpleName() + " preHandle failed.");
                }
            });
            // 遍历执行所有处理器
            chainingValues.getThingsHandlers().forEach(handlers -> handlers.getThingsHandling().doHandle(thingsRequest, thingsResponse));
            // 遍历执行所有后置拦截器
            chainingValues.getThingsInterceptors().forEach(interceptor -> interceptor.getThingsIntercepting().postHandle(thingsRequest, thingsResponse));
        } catch (Exception e) {
            exceptionValue.setException(e);
        } finally {
            // 遍历执行所有完成拦截器
            chainingValues.getThingsInterceptors().forEach(interceptor -> interceptor.getThingsIntercepting().afterCompletion(thingsRequest, thingsResponse, exceptionValue.getException()));
            cacheHandledMessage(thingsRequest, exceptionValue, cache);
        }
        return exceptionValue.getException() == null;
    }

    /**
     * 缓存已处理的消息id和失败次数，5分钟后过期
     *
     * @param thingsRequest
     * @param exceptionValue
     * @param cache
     */
    private void cacheHandledMessage(ThingsRequest thingsRequest, ExceptionValue exceptionValue, Cache<String, AtomicInteger> cache) {
        AtomicInteger atomicInteger = cache.get(thingsRequest.getTrm().getId());
        if (exceptionValue.getException() == null) {
            atomicInteger.set(-1);
        } else {
            atomicInteger.addAndGet(1);
        }
        cache.put(thingsRequest.getTrm().getId(), atomicInteger, TimeUnit.MINUTES.toMillis(5));
    }

    private ChainingValues getChainingValues(ThingsRequest thingsRequest, ThingsResponse thingsResponse, ThingsChainingType chainingType) {
        ChainingKeys keys = getKeys(thingsRequest.getTrm(), chainingType);
        return CHAINING_VALUES_CACHE.get(keys.getKeyFlag(), () -> {
            Set<ThingsInterceptors> interceptors = getInterceptors(keys, thingsRequest, chainingType);
            Set<ThingsHandlers> handlers = getHandlers(keys, thingsRequest, thingsResponse, chainingType);
            return new ChainingValues(handlers, interceptors);
        });
    }


    private Set<ThingsHandlers> getHandlers(ChainingKeys chainingKeys, ThingsRequest thingsRequest, ThingsResponse thingsResponse, ThingsChainingType chainingType) {
        return getChains(chainingKeys, thingsContainerManager.getThingsHandlersTable(), thingsRequest.getTrm(), i -> i.getThingsHandler().order(),
                v -> v.getChainingType().equals(chainingType) && v.getThingsHandling().canHandle(thingsRequest, thingsResponse));
    }

    private Set<ThingsInterceptors> getInterceptors(ChainingKeys chainingKeys, ThingsRequest thingsRequest, ThingsChainingType chainingType) {
        return getChains(chainingKeys, thingsContainerManager.getThingsInterceptorsTable(), thingsRequest.getTrm(), i -> i.getThingsInterceptor().order(), v -> v.getChainingType().equals(chainingType));
    }

    private <T> Set<T> getChains(ChainingKeys chainingKeys, Table<String, String, Set<T>> table, ThingsRequestMessage trm, Function<T, Integer> comparing, Predicate<T> filtering) {
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

    private ChainingKeys getKeys(ThingsRequestMessage trm, ThingsChainingType chainingType) {
        Set<ChainingKey> keys = new HashSet<>();
        ThingsMessageMethod baseMetadata = trm.getMessageMethod();
        ThingsMethodType thingsMethodType = ThingsMethodType.getMethodType(extractMiddlePart(trm.getMethod()));
        String identifies = subIdentifies(trm.getMethod());
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
        private final Set<ThingsHandlers> thingsHandlers;
        private final Set<ThingsInterceptors> thingsInterceptors;
    }

    @Data
    @AllArgsConstructor
    private static class ChainingKeys {
        private final Set<ChainingKey> keys;
        private final String keyFlag;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ExceptionValue {
        private Exception exception;
    }
}
