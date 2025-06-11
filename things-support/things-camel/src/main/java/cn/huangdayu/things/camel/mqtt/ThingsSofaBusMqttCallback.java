package cn.huangdayu.things.camel.mqtt;

import cn.huangdayu.things.api.sofabus.ThingsSofaBusCallback;
import cn.huangdayu.things.camel.CamelSofaBusRouteBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
public class ThingsSofaBusMqttCallback implements ThingsSofaBusCallback {

    private final ThingsSofaBusMqttClient client;

    @Override
    public void routeStoped(Object route) {
        if (route instanceof CamelSofaBusRouteBuilder builder) {
            if (client.isConnected()) {
                try {
                    client.unsubscribe(builder.getTopic());
                } catch (Exception e) {
                    log.error("Things SofaBus MqttClient [{}/{}] unsubscribe [{}] error.", client.getServerURI(), client.getClientId(), builder.getTopic(), e);
                }
            }
        }
    }
}
