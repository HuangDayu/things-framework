package cn.huangdayu.things.camel;

import cn.huangdayu.things.api.message.ThingsSubscriber;
import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusCallback;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.message.ThingsResponseMessage;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.common.wrapper.ThingsSubscribes;
import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Route;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.paho.mqtt5.PahoMqtt5Constants;
import org.apache.camel.support.RoutePolicySupport;

import java.util.UUID;

/**
 * @author huangdayu
 */
@Getter
@Slf4j
public class CamelSofaBusRouteBuilder extends RouteBuilder {

    private final String routeId;
    private final String topic;
    private final String endpointUri;
    private final CamelSofaBusConstructor constructor;
    private final ThingsSofaBus thingsSofaBus;
    private final ThingsSubscriber thingsSubscriber;
    private final ThingsSubscribes thingsSubscribes;
    private final ThingsSofaBusCallback callback;

    public CamelSofaBusRouteBuilder(String topic, String endpointUri, ThingsSofaBus thingsSofaBus, CamelSofaBusConstructor constructor,
                                    ThingsSubscribes thingsSubscribes, ThingsSubscriber thingsSubscriber, ThingsSofaBusCallback callback) {
        this.routeId = UUID.randomUUID().toString().split("-")[0];
        this.topic = topic;
        this.endpointUri = endpointUri;
        this.constructor = constructor;
        this.thingsSubscribes = thingsSubscribes;
        this.thingsSofaBus = thingsSofaBus;
        this.thingsSubscriber = thingsSubscriber;
        this.callback = callback;
    }

    @Override
    public void configure() throws Exception {
        from(endpointUri)
                .errorHandler(defaultErrorHandler()
                        .maximumRedeliveries(3)
                        .redeliveryDelay(5000)
                        .retryAttemptedLogLevel(LoggingLevel.WARN))
                .routeId(routeId)
                .routePolicy(new RoutePolicySupport() {
                    @Override
                    public void onStop(Route route) {
                        super.onStop(route);
                        if (callback != null) {
                            callback.routeStoped(CamelSofaBusRouteBuilder.this);
                        }
                    }
                })
                .onException(Exception.class)
                .logHandled(true)
                .maximumRedeliveries(3)
                .redeliveryDelay(1000)
                .useOriginalMessage()
                .handled(true)
                .end()
                .process(exchange -> {
                    try {
                        String receivedMessage = exchange.getIn().getBody(String.class);
                        ThingsResponseMessage thingsResponseMessage = JSON.to(ThingsResponseMessage.class, receivedMessage);
                        if (thingsResponseMessage.getResult() != null || thingsResponseMessage.getError() != null) {
                            ThingsResponse thingsResponse = ThingsResponse.builder().source(thingsSofaBus).type(thingsSofaBus.getType().name())
                                    .clientCode(constructor.getProperties().getClientId()).subscriber(thingsSubscribes.getSubscriber())
                                    .topic(exchange.getIn().getHeader(PahoMqtt5Constants.MQTT_TOPIC, String.class))
                                    .groupCode(constructor.getProperties().getGroupId()).trm(thingsResponseMessage).build();
                            log.debug("Things SofaBus Camel route response message {} to {} ", thingsResponseMessage.getId(), thingsSubscriber);
                            thingsSubscriber.input(new ThingsRequest(), thingsResponse);
                        } else {
                            ThingsRequestMessage thingsRequestMessage = JSON.to(ThingsRequestMessage.class, receivedMessage);
                            ThingsRequest thingsRequest = ThingsRequest.builder().source(thingsSofaBus).type(thingsSofaBus.getType().name())
                                    .clientCode(constructor.getProperties().getClientId()).subscriber(thingsSubscribes.getSubscriber())
                                    .topic(exchange.getIn().getHeader(PahoMqtt5Constants.MQTT_TOPIC, String.class))
                                    .groupCode(constructor.getProperties().getGroupId()).trm(thingsRequestMessage).build();
                            log.debug("Things SofaBus Camel route request message {} to {} ", thingsRequestMessage.getId(), thingsSubscriber);
                            thingsSubscriber.input(thingsRequest, new ThingsResponse());
                        }
                    } catch (Exception e) {
                        log.error("Things SofaBus Camel route process message error.", e);
                        exchange.setException(e);
                    }
                });
    }

}
