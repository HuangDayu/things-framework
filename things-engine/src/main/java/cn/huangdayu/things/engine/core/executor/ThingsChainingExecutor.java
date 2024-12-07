package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.message.ThingsFilterChain;
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
    public JsonThingsMessage doReceive(JsonThingsMessage jsonThingsMessage) {
        requestInterceptor(jsonThingsMessage);
        JsonThingsMessage response = handleMessage(jsonThingsMessage);
        responseInterceptor(response);
        filter(jsonThingsMessage, response);
        return response;
    }

    @Override
    public void doSubscribe(JsonThingsMessage jsonThingsMessage) {
        requestInterceptor(jsonThingsMessage);
        JsonThingsMessage response = handleMessage(jsonThingsMessage);
        responseInterceptor(response);
        filter(jsonThingsMessage, response);
    }

    @Override
    public JsonThingsMessage doSend(JsonThingsMessage jsonThingsMessage) {
        requestInterceptor(jsonThingsMessage);
        JsonThingsMessage response = sendMessage(jsonThingsMessage);
        responseInterceptor(response);
        filter(jsonThingsMessage, response);
        return response;
    }

    @Override
    public void doPublish(ThingsEventMessage thingsEventMessage) {
        doPublish(covertEventMessage(thingsEventMessage));
    }

    @Override
    public void doPublish(JsonThingsMessage jsonThingsMessage) {
        requestInterceptor(jsonThingsMessage);
        publishMessage(jsonThingsMessage);
        JsonThingsMessage response = jsonThingsMessage.success();
        responseInterceptor(response);
        filter(jsonThingsMessage, response);
    }

    private JsonThingsMessage handleMessage(JsonThingsMessage jsonThingsMessage) {
        for (ThingsHandler thingsHandler : handlerMap.values()) {
            if (thingsHandler.canHandle(jsonThingsMessage)) {
                return thingsHandler.doHandle(jsonThingsMessage);
            }
        }
        throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Can't handler this message");
    }

    private JsonThingsMessage sendMessage(JsonThingsMessage jsonThingsMessage) {
        for (ThingsHandler thingsHandler : handlerMap.values()) {
            if (thingsHandler.canHandle(jsonThingsMessage)) {
                return thingsHandler.doHandle(jsonThingsMessage);
            }
        }
        return thingsEndpointFactory.create(jsonThingsMessage).handleMessage(jsonThingsMessage);
    }

    private void publishMessage(JsonThingsMessage jsonThingsMessage) {
        for (ThingsHandler thingsHandler : handlerMap.values()) {
            THINGS_EXECUTOR.execute(() -> {
                if (thingsHandler.canHandle(jsonThingsMessage)) {
                    thingsHandler.doHandle(jsonThingsMessage);
                }
            });
        }
        THINGS_EXECUTOR.execute(() -> thingsEndpointFactory.create(jsonThingsMessage).handleEvent(jsonThingsMessage));
    }

    private void filter(JsonThingsMessage request, JsonThingsMessage response) {
        List<ThingsFilters> filters = getInterceptors(THINGS_FILTERS_TABLE, request, i -> i.getThingsFiltering().order());
        if (CollUtil.isNotEmpty(filters)) {
            handleFilters(new ThingsRequest(request), new ThingsResponse(response), filters);
        }
    }


    private void requestInterceptor(JsonThingsMessage jsonThingsMessage) {
        List<ThingsInterceptors> interceptors = getInterceptors(THINGS_REQUEST_INTERCEPTORS_TABLE, jsonThingsMessage, i -> i.getThingsIntercepting().order());
        if (CollUtil.isNotEmpty(interceptors)) {
            handleInterceptor(jsonThingsMessage, interceptors);
        }
    }

    private void responseInterceptor(JsonThingsMessage jsonThingsMessage) {
        List<ThingsInterceptors> interceptors = getInterceptors(THINGS_RESPONSE_INTERCEPTORS_TABLE, jsonThingsMessage, i -> i.getThingsIntercepting().order());
        if (CollUtil.isNotEmpty(interceptors)) {
            handleInterceptor(jsonThingsMessage, interceptors);
        }
    }


    private <T> List<T> getInterceptors(Table<String, String, Set<T>> table, JsonThingsMessage jsonThingsMessage, Function<T, Integer> function) {
        BaseThingsMetadata baseMetadata = jsonThingsMessage.getBaseMetadata();
        String identifies = subIdentifies(jsonThingsMessage.getMethod());
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


    private void handleInterceptor(JsonThingsMessage jsonThingsMessage, List<ThingsInterceptors> interceptors) {
        if (CollUtil.isNotEmpty(interceptors)) {
            for (ThingsInterceptors interceptor : interceptors) {
                ThingsServlet thingsServlet = new ThingsServlet(interceptor.getThingsIntercepting(), jsonThingsMessage);
                if (!interceptor.getThingsInterceptor().doIntercept(thingsServlet)) {
                    throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Things interceptor no passing.");
                }
            }
        }
    }

    private void handleFilters(ThingsRequest thingsRequest, ThingsResponse thingsResponse, List<ThingsFilters> thingsFilter) {
        ThingsFilterChain thingsFilterChain = new ThingsFilterChain(thingsFilter.stream().map(ThingsFilters::getThingsFilter).collect(Collectors.toList()));
        thingsFilterChain.doFilter(thingsRequest, thingsResponse);
    }

}
