package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.filters.ThingsFilterChain;
import cn.huangdayu.things.api.handler.ThingsHandler;
import cn.huangdayu.things.api.receiver.ThingsReceiver;
import cn.huangdayu.things.api.sender.ThingsSender;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.annotation.ThingsEvent;
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
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.BAD_REQUEST;
import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.*;
import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_WILDCARD;
import static cn.huangdayu.things.common.factory.ThreadPoolFactory.THINGS_EXECUTOR;
import static cn.huangdayu.things.common.utils.ThingsUtils.*;
import static cn.huangdayu.things.engine.core.executor.ThingsBaseExecutor.*;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
@RequiredArgsConstructor
public class ThingsChainingExecutor implements ThingsChaining, ThingsReceiver {

    private final Map<String, ThingsHandler> handlerMap;
    private final Map<String, ThingsSender> senderMap;

    @Override
    public JsonThingsMessage doReceive(JsonThingsMessage jsonThingsMessage) {
        requestInterceptor(jsonThingsMessage);
        JsonThingsMessage response = messageHandler(false, jsonThingsMessage);
        responseInterceptor(response);
        filter(jsonThingsMessage, response);
        return response;
    }

    @Override
    public void doSubscribe(JsonThingsMessage jsonThingsMessage) {
        requestInterceptor(jsonThingsMessage);
        JsonThingsMessage response = messageHandler(false, jsonThingsMessage);
        responseInterceptor(response);
        filter(jsonThingsMessage, response);
    }

    @Override
    public JsonThingsMessage send(JsonThingsMessage jsonThingsMessage) {
        requestInterceptor(jsonThingsMessage);
        JsonThingsMessage response = messageHandler(true, jsonThingsMessage);
        if (response == null) {
            response = messageSender(jsonThingsMessage);
        }
        responseInterceptor(response);
        filter(jsonThingsMessage, response);
        return response;
    }

    @Override
    public void publish(ThingsEventMessage thingsEventMessage) {
        JsonThingsMessage jsonThingsMessage = covertEventMessage(thingsEventMessage);
        requestInterceptor(jsonThingsMessage);
        eventHandler(jsonThingsMessage);
        JsonThingsMessage response = jsonThingsMessage.success();
        responseInterceptor(response);
        filter(jsonThingsMessage, response);
    }

    private JsonThingsMessage messageHandler(boolean send, JsonThingsMessage jsonThingsMessage) {
        for (ThingsHandler thingsHandler : handlerMap.values()) {
            if (thingsHandler.canHandle(jsonThingsMessage)) {
                return thingsHandler.doHandle(jsonThingsMessage);
            }
        }
        if (!send) {
            throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Handler the message failed.");
        }
        return messageSender(jsonThingsMessage);
    }

    private JsonThingsMessage messageSender(JsonThingsMessage jsonThingsMessage) {
        for (ThingsSender thingsSender : senderMap.values()) {
            if (thingsSender.canSend(jsonThingsMessage)) {
                return thingsSender.doSend(jsonThingsMessage);
            }
        }
        throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Send the message failed.");
    }

    private void eventHandler(JsonThingsMessage jsonThingsMessage) {
        for (ThingsHandler thingsHandler : handlerMap.values()) {
            THINGS_EXECUTOR.execute(() -> thingsHandler.doHandle(jsonThingsMessage));
        }
        for (ThingsSender thingsSender : senderMap.values()) {
            THINGS_EXECUTOR.execute(() -> thingsSender.doPublish(jsonThingsMessage));
        }
    }

    private JsonThingsMessage covertEventMessage(ThingsEventMessage message) {
        ThingsEvent thingsEvent = findBeanAnnotation(message, ThingsEvent.class);
        if (thingsEvent == null) {
            throw new ThingsException(null, BAD_REQUEST, "Message object is not ThingsEvent entry.");
        }
        JsonThingsMessage jsonThingsMessage = new JsonThingsMessage();
        jsonThingsMessage.setBaseMetadata(baseThingsMetadata -> {
            baseThingsMetadata.setProductCode(thingsEvent.productCode());
            baseThingsMetadata.setDeviceCode(message.getDeviceCode());
        });
        jsonThingsMessage.setQos(thingsEvent.qos());
        jsonThingsMessage.setPayload((JSONObject) JSON.toJSON(message, JSONWriter.Feature.WriteNulls));
        jsonThingsMessage.setMethod(EVENT_LISTENER_START_WITH.concat(thingsEvent.identifier()).concat(EVENT_TYPE_POST.replace(EVENT_TYPE, thingsEvent.type())));
        return jsonThingsMessage;
    }

    public void filter(JsonThingsMessage request, JsonThingsMessage response) {
        List<ThingsFilters> filters = getInterceptors(THINGS_FILTERS_TABLE, request, i -> i.getThingsFiltering().order());
        if (CollUtil.isNotEmpty(filters)) {
            handleFilters(new ThingsRequest(request), new ThingsResponse(response), filters);
        }
    }


    public void requestInterceptor(JsonThingsMessage jsonThingsMessage) {
        List<ThingsInterceptors> interceptors = getInterceptors(THINGS_REQUEST_INTERCEPTORS_TABLE, jsonThingsMessage, i -> i.getThingsIntercepting().order());
        if (CollUtil.isNotEmpty(interceptors)) {
            handleInterceptor(jsonThingsMessage, interceptors);
        }
    }

    public void responseInterceptor(JsonThingsMessage jsonThingsMessage) {
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
