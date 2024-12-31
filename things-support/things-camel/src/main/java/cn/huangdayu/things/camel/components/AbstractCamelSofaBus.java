package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.camel.CamelSofaBusConstructor;
import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsSofaBusProperties;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.DefaultComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.ERROR;
import static cn.huangdayu.things.common.enums.ThingsSofaBusType.*;

/**
 * @author huangdayu
 */
@Getter
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractCamelSofaBus implements ThingsSofaBus {
    protected final CamelContext camelContext;
    protected final CamelSofaBusConstructor constructor;
    protected DefaultComponent component;
    protected static final Map<ThingsSofaBusType, String> TOPIC_TEMPLATES = new HashMap<>();
    protected final Set<String> subscribed = new ConcurrentHashSet<>();


    public AbstractCamelSofaBus(CamelSofaBusConstructor constructor) {
        this.constructor = constructor;
        this.camelContext = constructor.getCamelContext();
    }

    static {
        TOPIC_TEMPLATES.put(AMQP, "${componentName}:queue:/things/message/${topicCode}");
        TOPIC_TEMPLATES.put(KAFKA, "${componentName}:/things/message/${topicCode}?groupId=${groupId}");
        TOPIC_TEMPLATES.put(MQTT, "${componentName}:/things/message/${topicCode}");
        TOPIC_TEMPLATES.put(ROCKETMQ, "${componentName}:topic:/things/message/${topicCode}?consumerGroup=${groupId}");
    }

    public abstract DefaultComponent buildComponent(ThingsSofaBusProperties properties);


    @Override
    public void init() {
        component = buildComponent(constructor.getProperties());
        if (CollUtil.isNotEmpty(constructor.getProperties().getProperties())) {
            constructor.getProperties().getProperties().forEach((k, v) -> component.getComponentPropertyConfigurer().configure(camelContext, component, k, v, false));
        }
        camelContext.addComponent(constructor.getProperties().getName(), component);
    }

    @Override
    public boolean output(String topic, ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        checkComponentInit();
        constructor.getProducerTemplate().sendBody(getTopic(topic), thingsRequest.getJtm().toString());
        return true;
    }

    private String getTopic(String topicCode) {
        String topicTemplates = TOPIC_TEMPLATES.get(getType());
        return topicTemplates.replace("${componentName}", constructor.getProperties().getName())
                .replace("${topicCode}", topicCode)
                .replace("${groupId}", constructor.getProperties().getGroupId());
    }

    @SneakyThrows
    @Override
    public boolean subscribe(String topicCode) {
        checkComponentInit();
        String topic = getTopic(topicCode);
        String routeId = getRouteId(topic);
        if (camelContext.getRoute(routeId) != null) {
            return false;
        }
        camelContext.addRoutes(new CustomRouteBuilder(this, routeId, topic, constructor));
        subscribed.add(routeId);
        return true;
    }

    @SneakyThrows
    @Override
    public boolean unsubscribe(String topicCode) {
        checkComponentInit();
        String topic = getTopic(topicCode);
        String routeId = getRouteId(topic);
        return camelContext.removeRoute(routeId) && subscribed.remove(routeId);
    }

    public String getRouteId(String topic) {
        return constructor.getProperties().hashCode() + "-" + topic;
    }


    @Override
    public boolean start() {
        checkComponentInit();
        if (component.isStarted()) {
            return true;
        }
        component.start();
        return true;
    }

    @Override
    public boolean stop() {
        checkComponentInit();
        subscribed.forEach(this::unsubscribe);
        if (component.isStopped()) {
            return true;
        }
        component.stop();
        return true;
    }

    @Override
    public boolean isStarted() {
        checkComponentInit();
        return component.isStarted();
    }

    private void checkComponentInit() {
        if (component == null) {
            throw new ThingsException(ERROR, "Things Component Not Init .");
        }
    }


    @Slf4j
    private static class CustomRouteBuilder extends RouteBuilder {
        private final String topic;
        private final String routeId;
        private final CamelSofaBusConstructor constructor;
        private final ThingsSofaBus thingsSofaBus;

        public CustomRouteBuilder(ThingsSofaBus thingsSofaBus, String routeId, String topic, CamelSofaBusConstructor constructor) {
            this.routeId = routeId;
            this.topic = topic;
            this.constructor = constructor;
            this.thingsSofaBus = thingsSofaBus;
        }

        @Override
        public void configure() throws Exception {
            from(topic)
                    .routeId(routeId)
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            String receivedMessage = exchange.getIn().getBody(String.class);
                            log.debug("Things Bus topic [{}] received message: {}", topic, receivedMessage);
                            JsonThingsMessage jtm = JSON.to(JsonThingsMessage.class, receivedMessage);
                            ThingsRequest thingsRequest = ThingsRequest.builder().source(thingsSofaBus).type(thingsSofaBus.getType().name())
                                    .endpoint(topic).clientCode(constructor.getProperties().getClientId()).groupCode(constructor.getProperties().getGroupId()).jtm(jtm).build();

                            ThingsResponse thingsResponse = ThingsResponse.builder().source(thingsSofaBus).type(thingsSofaBus.getType().name()).endpoint(topic)
                                    .clientCode(constructor.getProperties().getClientId()).groupCode(constructor.getProperties().getGroupId())
                                    .consumer(response -> {
                                        String replyMessage = response.getJtm().toString();
                                        log.debug("Things Bus topic [{}] reply message: {}", topic, replyMessage);
                                        constructor.getProducerTemplate().sendBody(topic, replyMessage);
                                    }).build();
                            constructor.getThingsChaining().input(thingsRequest, thingsResponse);
                        }
                    });
        }

    }


}
