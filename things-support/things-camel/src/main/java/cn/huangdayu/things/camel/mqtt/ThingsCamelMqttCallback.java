package cn.huangdayu.things.camel.mqtt;

import cn.huangdayu.things.camel.components.AbstractSofaBus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import java.util.concurrent.TimeUnit;

/**
 * @author huangdayu
 */
@Slf4j
@AllArgsConstructor
public class ThingsCamelMqttCallback implements MqttCallback {

    private CamelContext context;
    private MqttClient client;
    private AbstractSofaBus abstractSofaBus;
    private MqttConnectionOptions options;

    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {
        log.warn("MqttClient [{}/{}] disconnected", client.getServerURI(), client.getClientId());
    }

    @Override
    public void mqttErrorOccurred(MqttException exception) {
        log.warn("MqttClient [{}/{}] exception", client.getServerURI(), client.getClientId(), exception);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttToken token) {

    }

    /**
     * 如果重连，为确保所有路由正常，将所有路由重新加载启动
     *
     * @param reconnect If true, the connection was the result of automatic reconnect.
     * @param serverURI The server URI that the connection was made to.
     */
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        if (reconnect) {
            restartSofaBusRoutes();
        }
    }

    private void restartSofaBusRoutes() {
        for (String routeId : abstractSofaBus.getRouteIds()) {
            try {
                context.getRouteController().stopRoute(routeId, 5, TimeUnit.SECONDS);
                context.getRouteController().startRoute(routeId);
                log.info("MqttClient [{}/{}] authPacketArrived , restart route [{}] success.", client.getServerURI(), client.getClientId(), routeId);
            } catch (Exception e) {
                log.warn("MqttClient [{}/{}] authPacketArrived , restart route [{}] error.", client.getServerURI(), client.getClientId(), routeId, e);
            }
        }
    }

    private void reloadAllRoute() {
        try {
            context.getRouteController().reloadAllRoutes();
            log.info("MqttClient [{}/{}] connect complete , reload all routes success.", client.getServerURI(), client.getClientId());
        } catch (Exception e) {
            log.warn("MqttClient [{}/{}] connect complete , reload all routes error.", client.getServerURI(), client.getClientId(), e);
        }
    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {

    }
}
