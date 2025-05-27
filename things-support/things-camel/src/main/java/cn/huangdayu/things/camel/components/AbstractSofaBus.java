package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.camel.CamelSofaBusConstructor;
import cn.huangdayu.things.camel.CamelSofaBusRouteBuilder;
import cn.huangdayu.things.camel.ThingsSofaBusTopicValidator;
import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.common.wrapper.ThingsSubscribes;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.paho.mqtt5.PahoMqtt5Constants;
import org.apache.camel.spi.PropertyConfigurer;
import org.apache.camel.support.DefaultComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static cn.huangdayu.things.common.enums.ThingsSofaBusType.*;

/**
 * @author huangdayu
 */
@Getter
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSofaBus implements ThingsSofaBus {
    protected final CamelContext camelContext;
    protected final CamelSofaBusConstructor constructor;
    protected DefaultComponent component;
    protected static final ThingsSofaBusTopicValidator TOPIC_VALIDATOR = new ThingsSofaBusTopicValidator();
    protected static final Map<ThingsSofaBusType, String> ENDPOINT_URI_TEMPLATES = new HashMap<>();
    protected volatile Map<String, String> ROUTE_ID_MAP = new ConcurrentHashMap<>();

    protected abstract DefaultComponent buildComponent();

    public AbstractSofaBus(CamelSofaBusConstructor constructor) {
        this.constructor = constructor;
        this.camelContext = constructor.getCamelContext();
        this.component = buildComponent();
        if (CollUtil.isNotEmpty(constructor.getProperties().getProperties())) {
            PropertyConfigurer configurer = component.getComponentPropertyConfigurer();
            constructor.getProperties().getProperties().forEach((k, v) -> configurer.configure(camelContext, component, k, v, true));
        }
        camelContext.addComponent(constructor.getProperties().getName(), component);
    }

    static {
        ENDPOINT_URI_TEMPLATES.put(AMQP, "${componentName}:queue:${topic}");
        ENDPOINT_URI_TEMPLATES.put(KAFKA, "${componentName}:${topic}?groupId=${groupId}");
        ENDPOINT_URI_TEMPLATES.put(MQTT, "${componentName}:${topic}");
        ENDPOINT_URI_TEMPLATES.put(ROCKETMQ, "${componentName}:topic:${topic}?consumerGroup=${groupId}");
    }

    @Override
    public boolean output(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        JsonThingsMessage jtm = thingsRequest.getJtm();
        if (thingsResponse != null && thingsResponse.getJtm() != null) {
            jtm = thingsResponse.getJtm();
        }
        BaseThingsMetadata baseMetadata = jtm.getBaseMetadata();
        String topic = getTopic(ThingsSubscribes.builder().jtm(jtm).share(false).productCode(baseMetadata.getProductCode())
                .deviceCode(baseMetadata.getDeviceCode()).method(jtm.getMethod()).build());
        Endpoint endpoint = camelContext.getEndpoint(getEndpointUri(topic));
        constructor.getProducerTemplate().asyncSendBody(endpoint, jtm.toString());
        return true;
    }

    private String getEndpointUri(String topic) {
        String endpointUriTemplate = ENDPOINT_URI_TEMPLATES.get(getType());
        endpointUriTemplate = endpointUriTemplate.replace("${componentName}", constructor.getProperties().getName());
        endpointUriTemplate = endpointUriTemplate.replace("${topic}", topic);
        if (StrUtil.isNotBlank(constructor.getProperties().getGroupId())) {
            endpointUriTemplate = endpointUriTemplate.replace("${groupId}", constructor.getProperties().getGroupId());
        }
        return endpointUriTemplate;
    }

    protected String getTopic(ThingsSubscribes thingsSubscribes) {
        return String.format("things-%s-%s-%s", thingsSubscribes.getProductCode(), thingsSubscribes.getDeviceCode(), thingsSubscribes.getMethod());
    }

    @Override
    public boolean subscribe(ThingsSubscribes thingsSubscribes) {
        return subscribe(getTopic(thingsSubscribes));
    }

    @Override
    public boolean unsubscribe(ThingsSubscribes thingsSubscribes) {
        return unsubscribe(getTopic(thingsSubscribes));
    }

    @SneakyThrows
    private boolean subscribe(String topic) {
        if (isDuplicateSubscription(topic)) {
            log.warn("Things Bus subscribe topic [{}] duplicate subscription.", topic);
            return false;
        }
        String topicTemplate = getEndpointUri(topic);
        String routeId = UUID.randomUUID().toString();
        if (ROUTE_ID_MAP.containsKey(topicTemplate)) {
            log.warn("Things Bus subscribe topic [{}] is contains.", topic);
            return false;
        }
        camelContext.addRoutes(new CamelSofaBusRouteBuilder(this, routeId, topicTemplate, constructor));
        log.info("Things Bus topic [{}] subscribed for routeId [{}].", topic, routeId);
        ROUTE_ID_MAP.put(topicTemplate, routeId);
        return true;
    }

    @SneakyThrows
    private boolean unsubscribe(String topic) {
        String topicTemplate = getEndpointUri(topic);
        String routeId = ROUTE_ID_MAP.get(topicTemplate);
        log.info("Things Bus topic [{}] unsubscribe for routeId [{}].", topic, routeId);
        checkRemoveTopic(topic);
        return camelContext.removeRoute(routeId);
    }


    @Override
    public boolean start() {
        if (component.isStarted()) {
            return true;
        }
        component.start();
        return true;
    }

    @Override
    public boolean stop() {
        if (component.isStopped()) {
            return true;
        }
        component.stop();
        return true;
    }

    @Override
    public boolean isStarted() {
        return component.isStarted();
    }

    private boolean isDuplicateSubscription(String topic) {
        if (TOPIC_VALIDATOR.isDuplicateSubscription(topic)) {
            return true;
        }
        TOPIC_VALIDATOR.addTopic(topic);
        return false;
    }

    private boolean checkRemoveTopic(String topic) {
        return TOPIC_VALIDATOR.removeTopic(topic);
    }
}
