package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.message.ThingsSubscriber;
import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusCallback;
import cn.huangdayu.things.camel.CamelSofaBusConstructor;
import cn.huangdayu.things.camel.CamelSofaBusRouteBuilder;
import cn.huangdayu.things.camel.mqtt.ThingsSofaBusTopicValidator;
import cn.huangdayu.things.common.message.ThingsMessageMethod;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.common.wrapper.ThingsSubscribes;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
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
        ThingsRequestMessage trm = thingsRequest.getTrm();
        String topic = createTopic(ThingsSubscribes.builder().trm(trm).share(false).method(trm.getMessageMethod()).build());
        String endpointUri = createEndpointUri(topic, createParameterMap(thingsRequest));
        Endpoint endpoint = camelContext.getEndpoint(endpointUri);
        constructor.getProducerTemplate().asyncSendBody(endpoint, trm.toString());
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
        return Map.of("qos", String.valueOf(thingsRequest.getTrm().getQos()));
    }

    protected String createTopic(ThingsSubscribes thingsSubscribes) {
        return String.format("things-%s-%s", thingsSubscribes.getMethod().getProductCode(), thingsSubscribes.getMethod().getDeviceCode());
    }

    /**
     * 由于addRoutes操作存在耗时，这里做同步锁防止相同endpointUri重复创建的异常问题
     *
     * @param thingsSubscribes
     * @param thingsSubscriber
     * @return
     */
    @SneakyThrows
    @Override
    public synchronized boolean subscribe(ThingsSubscribes thingsSubscribes, ThingsSubscriber thingsSubscriber) {
        String topic = createTopic(thingsSubscribes);
        String endpointUri = createEndpointUri(topic, null);
        String routeId = ROUTE_MAP.get(endpointUri);
        if (StrUtil.isNotBlank(routeId) && camelContext.getRoute(routeId) != null) {
            log.warn("Things SofaBus subscribe topic [{}] is contains.", topic);
            return false;
        }
        CamelSofaBusRouteBuilder routeBuilder = new CamelSofaBusRouteBuilder(topic, endpointUri, this, constructor, thingsSubscribes, thingsSubscriber, getCallback());
        camelContext.addRoutes(routeBuilder);
        log.info("Things SofaBus [{}] subscribed topic [{}]  for routeId [{}] to subscribe handler [{}]",
                thingsSubscribes.getSubscriber(), topic, routeBuilder.getRouteId(), thingsSubscriber.getClass().getName());
        ROUTE_MAP.put(endpointUri, routeBuilder.getRouteId());
        return true;
    }

    @SneakyThrows
    @Override
    public boolean unsubscribe(ThingsSubscribes thingsSubscribes) {
        String topic = createTopic(thingsSubscribes);
        String topicTemplate = createEndpointUri(topic, null);
        String routeId = ROUTE_MAP.get(topicTemplate);
        camelContext.getRouteController().stopRoute(routeId);
        boolean result = camelContext.removeRoute(routeId);
        log.info("Things SofaBus stop route [{}] for unsubscribe topic [{}] the result: {}", routeId, topic, result);
        return result;
    }

    protected ThingsSofaBusCallback getCallback() {
        return null;
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
