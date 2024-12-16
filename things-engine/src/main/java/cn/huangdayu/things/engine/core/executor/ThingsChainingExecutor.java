package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.message.ThingsFilter;
import cn.huangdayu.things.api.message.ThingsHandler;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.message.ThingsEventMessage;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.common.wrapper.ThingsServlet;
import cn.huangdayu.things.engine.core.ThingsChaining;
import cn.huangdayu.things.engine.wrapper.ThingsFilters;
import cn.huangdayu.things.engine.wrapper.ThingsInterceptors;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.multi.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.BAD_REQUEST;
import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_WILDCARD;
import static cn.huangdayu.things.common.factory.ThreadPoolFactory.THINGS_EXECUTOR;
import static cn.huangdayu.things.common.utils.ThingsUtils.covertEventMessage;
import static cn.huangdayu.things.common.utils.ThingsUtils.subIdentifies;
import static cn.huangdayu.things.engine.core.executor.ThingsBaseExecutor.*;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
@RequiredArgsConstructor
public class ThingsChainingExecutor implements ThingsChaining {

    private final Map<String, ThingsHandler> handlerMap;
    private final ThingsEndpointFactory thingsEndpointFactory;

    @Override
    public JsonThingsMessage doReceive(JsonThingsMessage jtm) {
        requestInterceptor(jtm);
        JsonThingsMessage response = handleMessage(jtm);
        responseInterceptor(response);
        filter(jtm, response);
        return response;
    }

    @Override
    public void doSubscribe(JsonThingsMessage jtm) {
        requestInterceptor(jtm);
        JsonThingsMessage response = handleMessage(jtm);
        responseInterceptor(response);
        filter(jtm, response);
    }

    @Override
    public JsonThingsMessage doSend(JsonThingsMessage jtm) {
        requestInterceptor(jtm);
        JsonThingsMessage response = sendMessage(jtm);
        responseInterceptor(response);
        filter(jtm, response);
        return response;
    }

    @Override
    public void doPublish(ThingsEventMessage tem) {
        doPublish(covertEventMessage(tem));
    }

    @Override
    public void doPublish(JsonThingsMessage jtm) {
        requestInterceptor(jtm);
        publishMessage(jtm);
        JsonThingsMessage response = jtm.success();
        responseInterceptor(response);
        filter(jtm, response);
    }

    @Override
    public Mono<JsonThingsMessage> doReactorReceive(JsonThingsMessage jtm) {
        requestInterceptor(jtm);
        Mono<JsonThingsMessage> response = handleReactorMessage(jtm);
        return response.filter(response1 -> {
            responseInterceptor(response1);
            filter(jtm, response1);
            return true;
        });
    }

    @Override
    public Mono<JsonThingsMessage> doReactorSend(JsonThingsMessage jtm) {
        requestInterceptor(jtm);
        Mono<JsonThingsMessage> response = sendReactorMessage(jtm);
        return response.filter(response1 -> {
            responseInterceptor(response1);
            filter(jtm, response1);
            return true;
        });
    }

    private JsonThingsMessage handleMessage(JsonThingsMessage jtm) {
        for (ThingsHandler thingsHandler : handlerMap.values()) {
            if (thingsHandler.canHandle(jtm)) {
                return thingsHandler.syncHandler(jtm);
            }
        }
        throw new ThingsException(jtm, BAD_REQUEST, "Can't handler this message");
    }

    private Mono<JsonThingsMessage> handleReactorMessage(JsonThingsMessage jtm) {
        for (ThingsHandler thingsHandler : handlerMap.values()) {
            if (thingsHandler.canHandle(jtm)) {
                return thingsHandler.reactorHandler(jtm);
            }
        }
        throw new ThingsException(jtm, BAD_REQUEST, "Can't handler this message");
    }

    private JsonThingsMessage sendMessage(JsonThingsMessage jtm) {
        for (ThingsHandler thingsHandler : handlerMap.values()) {
            if (thingsHandler.canHandle(jtm)) {
                return thingsHandler.syncHandler(jtm);
            }
        }
        return thingsEndpointFactory.create(jtm).handleMessage(jtm);
    }

    private Mono<JsonThingsMessage> sendReactorMessage(JsonThingsMessage jtm) {
        for (ThingsHandler thingsHandler : handlerMap.values()) {
            if (thingsHandler.canHandle(jtm)) {
                return thingsHandler.reactorHandler(jtm);
            }
        }
        return thingsEndpointFactory.create(jtm, true).reactorMessage(jtm);
    }

    private void publishMessage(JsonThingsMessage jtm) {
        for (ThingsHandler thingsHandler : handlerMap.values()) {
            THINGS_EXECUTOR.execute(() -> {
                if (thingsHandler.canHandle(jtm)) {
                    thingsHandler.syncHandler(jtm);
                }
            });
        }
        THINGS_EXECUTOR.execute(() -> thingsEndpointFactory.create(jtm).handleEvent(jtm));
    }

    private void filter(JsonThingsMessage request, JsonThingsMessage response) {
        List<ThingsFilters> filters = getInterceptors(THINGS_FILTERS_TABLE, request, i -> i.getThingsFiltering().order());
        if (CollUtil.isNotEmpty(filters)) {
            handleFilters(new ThingsRequest(request), new ThingsResponse(response), filters);
        }
    }


    private void requestInterceptor(JsonThingsMessage jtm) {
        List<ThingsInterceptors> interceptors = getInterceptors(THINGS_REQUEST_INTERCEPTORS_TABLE, jtm, i -> i.getThingsIntercepting().order());
        if (CollUtil.isNotEmpty(interceptors)) {
            handleInterceptor(jtm, interceptors);
        }
    }

    private void responseInterceptor(JsonThingsMessage jtm) {
        List<ThingsInterceptors> interceptors = getInterceptors(THINGS_RESPONSE_INTERCEPTORS_TABLE, jtm, i -> i.getThingsIntercepting().order());
        if (CollUtil.isNotEmpty(interceptors)) {
            handleInterceptor(jtm, interceptors);
        }
    }


    private <T> List<T> getInterceptors(Table<String, String, Set<T>> table, JsonThingsMessage jtm, Function<T, Integer> function) {
        BaseThingsMetadata baseMetadata = jtm.getBaseMetadata();
        String identifies = subIdentifies(jtm.getMethod());
        Set<T> linkedHashSet = new LinkedHashSet<>();
        Set<T> set1 = table.get(identifies, THINGS_WILDCARD);
        if (CollUtil.isNotEmpty(set1)) {
            linkedHashSet.addAll(set1);
        }
        Set<T> set2 = table.get(THINGS_WILDCARD, baseMetadata.getProductCode());
        if (CollUtil.isNotEmpty(set2)) {
            linkedHashSet.addAll(set2);
        }
        Set<T> set3 = table.get(identifies, baseMetadata.getProductCode());
        if (CollUtil.isNotEmpty(set3)) {
            linkedHashSet.addAll(set3);
        }
        Set<T> set4 = table.get(THINGS_WILDCARD, THINGS_WILDCARD);
        if (CollUtil.isNotEmpty(set4)) {
            linkedHashSet.addAll(set4);
        }
        return linkedHashSet.stream().sorted(Comparator.comparing(function)).collect(Collectors.toList());
    }


    private void handleInterceptor(JsonThingsMessage jtm, List<ThingsInterceptors> interceptors) {
        if (CollUtil.isNotEmpty(interceptors)) {
            for (ThingsInterceptors interceptor : interceptors) {
                ThingsServlet thingsServlet = new ThingsServlet(interceptor.getThingsIntercepting(), jtm);
                if (!interceptor.getThingsInterceptor().doIntercept(thingsServlet)) {
                    throw new ThingsException(jtm, BAD_REQUEST, "Things interceptor no passing.");
                }
            }
        }
    }

    private void handleFilters(ThingsRequest thingsRequest, ThingsResponse thingsResponse, List<ThingsFilters> thingsFilter) {
        ThingsFilter.Chain chain = new ThingsFilter.Chain(thingsFilter.stream().map(ThingsFilters::getThingsFilter).collect(Collectors.toList()));
        chain.doFilter(thingsRequest, thingsResponse);
    }

}
