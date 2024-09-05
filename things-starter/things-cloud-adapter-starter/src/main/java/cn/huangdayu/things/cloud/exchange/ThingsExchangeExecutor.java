package cn.huangdayu.things.cloud.exchange;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.async.ThingsCacheMessageEvent;
import cn.huangdayu.things.engine.chaining.sender.ThingsSender;
import cn.huangdayu.things.engine.common.ThingsConstants;
import cn.huangdayu.things.engine.configuration.ThingsEngineProperties;
import cn.huangdayu.things.engine.core.ThingsInstancesEngine;
import cn.huangdayu.things.engine.core.ThingsObserverEngine;
import cn.huangdayu.things.engine.exception.ThingsException;
import cn.huangdayu.things.cloud.exchange.send.ThingsMessageSender;
import cn.huangdayu.things.engine.message.BaseThingsMetadata;
import cn.huangdayu.things.engine.message.JsonThingsMessage;
import cn.huangdayu.things.engine.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static cn.huangdayu.things.engine.common.ThingsConstants.ErrorCodes.BAD_REQUEST;
import static cn.huangdayu.things.engine.common.ThingsConstants.ErrorCodes.NOT_FOUND;
import static cn.huangdayu.things.engine.common.ThingsUtils.getUUID;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean(order = 1)
@RequiredArgsConstructor
public class ThingsExchangeExecutor implements ThingsSender {

    private final static Map<String, ThingsMessageSender> SENDER_MAP = new HashMap<>();
    public static final String CACHE = "cache";
    private final Map<String, ThingsMessageSender> thingsMessageSender;
    private final ThingsInstancesEngine thingsInstancesEngine;
    private final ThingsEngineProperties thingsEngineProperties;
    public final ThingsObserverEngine thingsObserverEngine;

    @PostConstruct
    private void init() {
        thingsMessageSender.forEach((key, sender) -> SENDER_MAP.put(sender.getProtocol(), sender));
        thingsObserverEngine.registerObserver(ThingsCacheMessageEvent.class, event -> doSend(event.getJsonThingsMessage()));
    }


    @Override
    public JsonThingsMessage doSend(JsonThingsMessage jsonThingsMessage) {
        try {
            Set<ThingsInstance> targetInstances = getTargetInstances(jsonThingsMessage);
            if (CollUtil.isNotEmpty(targetInstances)) {
                addInstanceId(jsonThingsMessage);
                return send(targetInstances, jsonThingsMessage);
            }
        } catch (Exception e) {
            log.error("Things exchange send message exception: {}", e.getMessage());
        }
        return defaultHandler(jsonThingsMessage);
    }


    private JsonThingsMessage defaultHandler(JsonThingsMessage jsonThingsMessage) {
        try {
            ThingsMessageSender thingsMessageSender = SENDER_MAP.get(thingsEngineProperties.getUpstreamProtocol());
            if (thingsMessageSender != null) {
                return thingsMessageSender.handler(null, jsonThingsMessage);
            }
        } catch (Exception e) {
            log.warn("Things exchange send message to {} protocol exception: {}", thingsEngineProperties.getUpstreamProtocol(), e.getMessage());
        }
        log.warn("Things no found sender for protocol {} , the message is save to cache.", thingsEngineProperties.getUpstreamProtocol());
        if (jsonThingsMessage.getQos() == 0) {
            throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Things message send failed.", getUUID());
        }
        return SENDER_MAP.get(CACHE).handler(null, jsonThingsMessage);
    }


    private Set<ThingsInstance> getTargetInstances(JsonThingsMessage request) {
        BaseThingsMetadata baseMetadata = request.getBaseMetadata();
        if (request.isResponse() && StrUtil.isNotBlank(baseMetadata.getSource())) {
            return Set.of(ThingsInstance.valueOf(baseMetadata.getSource()));
        }
        if (StrUtil.isNotBlank(baseMetadata.getTarget())) {
            return Set.of(ThingsInstance.valueOf(baseMetadata.getTarget()));
        }
        if (request.getMethod().startsWith(ThingsConstants.Methods.EVENT_LISTENER_START_WITH)) {
            return thingsInstancesEngine.getConsumeInstances(baseMetadata.getProductCode(), baseMetadata.getDeviceCode(), request.getMethod());
        }
        return thingsInstancesEngine.getProvideInstances(baseMetadata.getProductCode(), baseMetadata.getDeviceCode(), request.getMethod());
    }

    private JsonThingsMessage send(Set<ThingsInstance> instances, JsonThingsMessage thingsMessage) {
        List<JsonThingsMessage> response = sendAll(instances, thingsMessage);
        if (CollUtil.isNotEmpty(response)) {
            if (response.size() == 1) {
                return response.getFirst();
            }
            for (JsonThingsMessage jsonThingsMessage : response) {
                if (jsonThingsMessage != null && !NOT_FOUND.equals(jsonThingsMessage.getBaseMetadata().getErrorCode())) {
                    return jsonThingsMessage;
                }
            }
        }
        throw new ThingsException(thingsMessage, BAD_REQUEST, "Things message send instances is empty.", getUUID());
    }

    public List<JsonThingsMessage> sendAll(Set<ThingsInstance> thingsInstances, JsonThingsMessage jsonThingsMessage) {
        if (CollUtil.isNotEmpty(thingsInstances)) {
            List<JsonThingsMessage> responses = new LinkedList<>();
            for (ThingsInstance thingsInstance : thingsInstances) {
                JsonThingsMessage response = send(thingsInstance, jsonThingsMessage);
                if (response != null) {
                    responses.add(response);
                }
            }
            return responses;
        }
        throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Things instances is empty.", getUUID());
    }

    public JsonThingsMessage send(ThingsInstance thingsInstance, JsonThingsMessage jsonThingsMessage) {
        if (thingsInstance != null) {
            ThingsMessageSender sender = SENDER_MAP.get(thingsInstance.getProtocol());
            if (sender != null) {
                return sender.handler(thingsInstance, jsonThingsMessage);
            }
            throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Things instance not found sender.", getUUID());
        }
        throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Things instances is null.", getUUID());
    }

    private void addInstanceId(JsonThingsMessage message) {
        message.setBaseMetadata(baseThingsMetadata -> {
            if (StrUtil.isNotBlank(baseThingsMetadata.getSource()) && StrUtil.isBlank(baseThingsMetadata.getTarget())) {
                baseThingsMetadata.setTarget(baseThingsMetadata.getSource());
            }
            baseThingsMetadata.setSource(thingsInstancesEngine.getThingsInstance().toString());
        });
    }
}
