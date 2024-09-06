package cn.huangdayu.things.cloud.exchange;

import cn.huangdayu.things.cloud.exchange.send.EndpointSender;
import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.async.ThingsCacheMessageEvent;
import cn.huangdayu.things.engine.chaining.sender.ThingsSender;
import cn.huangdayu.things.engine.configuration.ThingsEngineProperties;
import cn.huangdayu.things.engine.core.ThingsInstancesEngine;
import cn.huangdayu.things.engine.core.ThingsObserverEngine;
import cn.huangdayu.things.engine.exception.ThingsException;
import cn.huangdayu.things.engine.message.JsonThingsMessage;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static cn.huangdayu.things.engine.common.ThingsConstants.ErrorCodes.BAD_REQUEST;
import static cn.huangdayu.things.engine.common.ThingsUtils.getUUID;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean(order = 1)
@RequiredArgsConstructor
public class ThingsEndpointSender implements ThingsSender {

    private final static Map<String, EndpointSender> SENDER_MAP = new HashMap<>();
    public static final String RETRY = "retry";
    public static final String SCHEMA = "://";
    private final Map<String, EndpointSender> thingsMessageSender;
    private final ThingsInstancesEngine thingsInstancesEngine;
    private final ThingsEngineProperties thingsEngineProperties;
    public final ThingsObserverEngine thingsObserverEngine;
    private final ThingsEndpointGetter thingsEndpointGetter;

    @PostConstruct
    private void init() {
        thingsMessageSender.forEach((key, sender) -> SENDER_MAP.put(sender.endpointProtocol(), sender));
        thingsObserverEngine.registerObserver(ThingsCacheMessageEvent.class, event -> doPublish(event.getJsonThingsMessage()));
    }


    @Override
    public boolean canSend(JsonThingsMessage message) {
        String targetEndpointUri = thingsEndpointGetter.getTargetEndpointUri(message);
        if (StrUtil.isNotBlank(targetEndpointUri)) {
            String[] split = targetEndpointUri.split(SCHEMA);
            EndpointSender endpointSender = SENDER_MAP.get(split[0]);
            return endpointSender != null;
        }
        return false;
    }

    @Override
    public JsonThingsMessage doSend(JsonThingsMessage jsonThingsMessage) {
        String targetEndpointUri = thingsEndpointGetter.getTargetEndpointUri(jsonThingsMessage);
        if (StrUtil.isNotBlank(targetEndpointUri)) {
            String[] split = targetEndpointUri.split(SCHEMA);
            EndpointSender endpointSender = SENDER_MAP.get(split[0]);
            if (endpointSender != null) {
                addInstanceId(jsonThingsMessage);
                return endpointSender.handler(split[1], jsonThingsMessage);
            }
        }
        throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Not found target things endpointUri.", getUUID());
    }

    @Override
    public void doPublish(JsonThingsMessage jsonThingsMessage) {
        Set<String> targetEndpointUris = thingsEndpointGetter.getTargetEndpointUris(jsonThingsMessage);
        if (CollUtil.isNotEmpty(targetEndpointUris)) {
            for (String targetEndpointUri : targetEndpointUris) {
                String[] split = targetEndpointUri.split(SCHEMA);
                EndpointSender endpointSender = SENDER_MAP.get(split[0]);
                if (endpointSender != null) {
                    addInstanceId(jsonThingsMessage);
                    endpointSender.handler(split[1], jsonThingsMessage);
                }
            }
        } else {
            if (jsonThingsMessage.getQos() == 0) {
                throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Things event publish failed.", getUUID());
            }
            SENDER_MAP.get(RETRY).handler(null, jsonThingsMessage);
        }
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
