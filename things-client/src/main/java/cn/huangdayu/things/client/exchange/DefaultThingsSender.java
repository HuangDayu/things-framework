package cn.huangdayu.things.client.exchange;

import cn.huangdayu.things.api.endpoint.ThingsEndpointGetter;
import cn.huangdayu.things.api.endpoint.ThingsEndpointSender;
import cn.huangdayu.things.api.instances.ThingsInstancesGetter;
import cn.huangdayu.things.api.sender.ThingsSender;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.event.ThingsCacheMessageEvent;
import cn.huangdayu.things.common.event.ThingsEventObserver;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.BAD_REQUEST;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean(order = 1)
@RequiredArgsConstructor
public class DefaultThingsSender implements ThingsSender {

    private final static Map<String, ThingsEndpointSender> SENDER_MAP = new HashMap<>();
    public static final String RETRY = "retry";
    public static final String SCHEMA = "://";
    private final Map<String, ThingsEndpointSender> thingsMessageSender;
    private final ThingsInstancesGetter thingsInstancesGetter;
    private final ThingsEventObserver thingsObserverEngine;
    private final ThingsEndpointGetter thingsEndpointGetter;

    @PostConstruct
    private void init() {
        thingsMessageSender.forEach((key, sender) -> SENDER_MAP.put(sender.endpointProtocol(), sender));
        thingsObserverEngine.registerObserver(ThingsCacheMessageEvent.class, event -> doPublish(event.getJsonThingsMessage()));
    }


    @Override
    public boolean canSend(JsonThingsMessage message) {
        String targetEndpointUri = thingsEndpointGetter.getSendUri(message);
        if (StrUtil.isNotBlank(targetEndpointUri)) {
            String[] split = targetEndpointUri.split(SCHEMA);
            ThingsEndpointSender thingsEndpointSender = SENDER_MAP.get(split[0]);
            return thingsEndpointSender != null;
        }
        return false;
    }

    @Override
    public JsonThingsMessage doSend(JsonThingsMessage jsonThingsMessage) {
        String targetEndpointUri = thingsEndpointGetter.getSendUri(jsonThingsMessage);
        if (StrUtil.isNotBlank(targetEndpointUri)) {
            String[] split = targetEndpointUri.split(SCHEMA);
            ThingsEndpointSender thingsEndpointSender = SENDER_MAP.get(split[0]);
            if (thingsEndpointSender != null) {
                addInstanceId(jsonThingsMessage);
                return thingsEndpointSender.handler(split[1], jsonThingsMessage);
            }
        }
        throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Not found target things endpointUri.");
    }

    @Override
    public void doPublish(JsonThingsMessage jsonThingsMessage) {
        Set<String> targetEndpointUris = thingsEndpointGetter.getPublishUris(jsonThingsMessage);
        if (CollUtil.isNotEmpty(targetEndpointUris)) {
            for (String targetEndpointUri : targetEndpointUris) {
                String[] split = targetEndpointUri.split(SCHEMA);
                ThingsEndpointSender thingsEndpointSender = SENDER_MAP.get(split[0]);
                if (thingsEndpointSender != null) {
                    addInstanceId(jsonThingsMessage);
                    thingsEndpointSender.handler(split[1], jsonThingsMessage);
                }
            }
        } else {
            if (jsonThingsMessage.getQos() == 0) {
                throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Things event publish failed.");
            }
            SENDER_MAP.get(RETRY).handler(null, jsonThingsMessage);
        }
    }

    private void addInstanceId(JsonThingsMessage message) {
        message.setBaseMetadata(baseThingsMetadata -> {
            if (StrUtil.isNotBlank(baseThingsMetadata.getSource()) && StrUtil.isBlank(baseThingsMetadata.getTarget())) {
                baseThingsMetadata.setTarget(baseThingsMetadata.getSource());
            }
            baseThingsMetadata.setSource(thingsInstancesGetter.getInstanceCode());
        });
    }
}
