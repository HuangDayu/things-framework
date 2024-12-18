package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.common.enums.ThingsComponentType;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsComponentProperties;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.DefaultComponent;

import java.util.HashMap;
import java.util.Map;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.ERROR;

/**
 * @author huangdayu
 */
@Getter
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractCamelSofaBus implements ThingsSofaBus {
    private final CamelContext camelContext;
    private final ProducerTemplate producerTemplate;
    private ThingsComponentProperties properties;
    private DefaultComponent component;
    private final static Map<ThingsComponentType, String> FROM_URI_TEMPLATES = new HashMap<>();
    private final static Map<ThingsComponentType, String> TO_URI_TEMPLATES = new HashMap<>();

    static {
        FROM_URI_TEMPLATES.put(ThingsComponentType.AMQP, "amqp:queue:%s");
        FROM_URI_TEMPLATES.put(ThingsComponentType.KAFKA, "kafka:%s?groupId=%s");
        FROM_URI_TEMPLATES.put(ThingsComponentType.MQTT, "paho:%s");
        FROM_URI_TEMPLATES.put(ThingsComponentType.ROCKETMQ, "rocketmq:topic:%s?consumerGroup=%s");

        TO_URI_TEMPLATES.put(ThingsComponentType.AMQP, "amqp:queue:%s");
        TO_URI_TEMPLATES.put(ThingsComponentType.KAFKA, "kafka:%s");
        TO_URI_TEMPLATES.put(ThingsComponentType.MQTT, "paho:%s");
        TO_URI_TEMPLATES.put(ThingsComponentType.ROCKETMQ, "rocketmq:topic:%s");
    }

    public abstract DefaultComponent buildComponent(ThingsComponentProperties properties);


    @Override
    public void init(ThingsComponentProperties properties) {
        component = buildComponent(properties);
        if (CollUtil.isNotEmpty(properties.getProperties())) {
            properties.getProperties().forEach((k, v) -> component.getComponentPropertyConfigurer().configure(camelContext, component, k, v, false));
        }
        camelContext.addComponent(properties.getName(), component);
        this.properties = properties;
    }

    @Override
    public boolean output(String topic, ThingsRequest thingsRequest) {
        checkComponentInit();
        String toUri = String.format(TO_URI_TEMPLATES.get(getType()), topic, properties.getGroupId());
        producerTemplate.sendBody(toUri, thingsRequest.getJtm().toString());
        return true;
    }

    @SneakyThrows
    @Override
    public boolean subscribe(String topic, ThingsChaining thingsChaining) {
        checkComponentInit();
        String fromUri = String.format(FROM_URI_TEMPLATES.get(getType()), topic, properties.getGroupId());
        String toUri = String.format(TO_URI_TEMPLATES.get(getType()), topic, properties.getGroupId());
        String routeId = getRouteId(topic);
        if (camelContext.getRoute(routeId) != null) {
            return false;
        }
        camelContext.addRoutes(new CustomRouteBuilder(this, routeId, fromUri, toUri, thingsChaining, properties));
        return true;
    }

    @SneakyThrows
    @Override
    public boolean unsubscribe(String topic) {
        checkComponentInit();
        String routeId = getRouteId(topic);
        return camelContext.removeRoute(routeId);
    }

    public String getRouteId(String topic) {
        return properties.hashCode() + "-" + topic;
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
        private final String routeId;
        private final String fromUri;
        private final String toUri;
        private final ThingsChaining thingsChaining;
        private final ThingsSofaBus thingsSofaBus;
        private final ThingsComponentProperties properties;

        public CustomRouteBuilder(ThingsSofaBus thingsSofaBus, String routeId, String fromUri, String toUri, ThingsChaining thingsChaining, ThingsComponentProperties properties) {
            this.routeId = routeId;
            this.fromUri = fromUri;
            this.toUri = toUri;
            this.thingsChaining = thingsChaining;
            this.thingsSofaBus = thingsSofaBus;
            this.properties = properties;
        }

        @Override
        public void configure() throws Exception {
            from(fromUri)
                    .routeId(routeId)
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            String receivedMessage = exchange.getIn().getBody(String.class);
                            log.debug("Things Bus topic [{}] received message: {}", fromUri, receivedMessage);
                            JsonThingsMessage jtm = JSON.to(JsonThingsMessage.class, receivedMessage);
                            ThingsRequest thingsRequest = new ThingsRequest(thingsSofaBus, thingsSofaBus.getType().name(), fromUri, properties.getClientId(), properties.getGroupId(), null, jtm);
                            ThingsResponse thingsResponse = new ThingsResponse(thingsSofaBus, thingsSofaBus.getType().name(), fromUri, properties.getClientId(), properties.getGroupId(), null, null);
                            thingsChaining.input(thingsRequest, thingsResponse);
                            String replyMessage = thingsResponse.getJtm().toString();
                            log.debug("Things Bus topic [{}] reply message: {}", toUri, replyMessage);
                            exchange.getMessage().setBody(replyMessage);
                        }
                    })
                    .to(toUri);
        }

    }

}
