package cn.huangdayu.things.camel;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.paho.mqtt5.PahoMqtt5Constants;

/**
 * @author huangdayu
 */

@Slf4j
public class CamelSofaBusRouteBuilder extends RouteBuilder {
    private final String topic;
    private final String routeId;
    private final CamelSofaBusConstructor constructor;
    private final ThingsSofaBus thingsSofaBus;

    public CamelSofaBusRouteBuilder(ThingsSofaBus thingsSofaBus, String routeId, String topic, CamelSofaBusConstructor constructor) {
        this.routeId = routeId;
        this.topic = topic;
        this.constructor = constructor;
        this.thingsSofaBus = thingsSofaBus;
    }

    @Override
    public void configure() throws Exception {
        from(topic)
                .routeId(routeId)
                .onException(Exception.class)
                .maximumRedeliveries(3)
                .redeliveryDelay(1000)
                .useOriginalMessage()
                .handled(true)
                .end()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String receivedMessage = exchange.getIn().getBody(String.class);
                        JsonThingsMessage jtm = JSON.to(JsonThingsMessage.class, receivedMessage);
                        ThingsRequest thingsRequest = ThingsRequest.builder().source(thingsSofaBus).type(thingsSofaBus.getType().name())
                                .topic(exchange.getIn().getHeader(PahoMqtt5Constants.MQTT_TOPIC, String.class)).clientCode(constructor.getProperties().getClientId())
                                .groupCode(constructor.getProperties().getGroupId()).jtm(jtm).build();
                        constructor.getThingsChaining().input(thingsRequest, new ThingsResponse());
                    }
                });
    }

}
