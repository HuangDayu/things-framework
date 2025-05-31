package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.camel.CamelSofaBusConstructor;
import cn.huangdayu.things.camel.CamelSofaBusRouteBuilder;
import cn.huangdayu.things.camel.mqtt.ThingsSofaBusTopicValidator;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.common.wrapper.ThingsSubscribes;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.spi.PropertyConfigurer;
import org.apache.camel.support.DefaultComponent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
    protected volatile Map<String, String> ROUTE_MAP = new ConcurrentHashMap<>();

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

    @Override
    public boolean output(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        JsonThingsMessage jtm = thingsRequest.getJtm();
        BaseThingsMetadata baseMetadata = jtm.getBaseMetadata();
        String topic = createTopic(ThingsSubscribes.builder().jtm(jtm).share(false).productCode(baseMetadata.getProductCode())
                .deviceCode(baseMetadata.getDeviceCode()).method(jtm.getMethod()).build());
        String endpointUri = createEndpointUri(topic, createParameterMap(thingsRequest));
        Endpoint endpoint = camelContext.getEndpoint(endpointUri);
        constructor.getProducerTemplate().asyncSendBody(endpoint, jtm.toString());
        thingsRequest.setTarget(this);
        thingsRequest.setType(getType().name());
        thingsRequest.setTopic(endpointUri);
        thingsRequest.setClientCode(constructor.getProperties().getClientId());
        thingsRequest.setGroupCode(constructor.getProperties().getGroupId());
        return true;
    }

    protected String createEndpointUri(String topic, Map<String, String> parameterMap) {
        String parameter = CollUtil.isNotEmpty(parameterMap) ? "?" + MapUtil.join(parameterMap, "&", "=") : "";
        return String.format("%s:%s%s", constructor.getProperties().getName(), topic, parameter);
    }

    protected Map<String, String> createParameterMap(ThingsRequest thingsRequest) {
        return Map.of("qos", String.valueOf(thingsRequest.getJtm().getQos()));
    }

    protected String createTopic(ThingsSubscribes thingsSubscribes) {
        return String.format("things-%s-%s", thingsSubscribes.getProductCode(), thingsSubscribes.getDeviceCode());
    }

    @SneakyThrows
    @Override
    public boolean subscribe(ThingsSubscribes thingsSubscribes) {
        String topic = createTopic(thingsSubscribes);
        String topicTemplate = createEndpointUri(topic, null);
        if (ROUTE_MAP.containsKey(topicTemplate) && camelContext.getRoute(ROUTE_MAP.get(topicTemplate)) != null) {
            log.warn("Things Bus subscribe topic [{}] is contains.", topic);
            return false;
        }
        String routeId = UUID.randomUUID().toString().split("-")[0];
        camelContext.addRoutes(new CamelSofaBusRouteBuilder(this, routeId, topicTemplate, constructor));
        log.info("Things Bus topic [{}] subscribed for routeId [{}].", topic, routeId);
        ROUTE_MAP.put(topicTemplate, routeId);
        return true;
    }

    @SneakyThrows
    @Override
    public boolean unsubscribe(ThingsSubscribes thingsSubscribes) {
        String topic = createTopic(thingsSubscribes);
        String topicTemplate = createEndpointUri(topic, null);
        String routeId = ROUTE_MAP.get(topicTemplate);
        log.info("Things Bus topic [{}] unsubscribe for routeId [{}].", topic, routeId);
        return camelContext.removeRoute(routeId);
    }

    public Set<String> getRouteIds() {
        return new HashSet<>(ROUTE_MAP.values());
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
