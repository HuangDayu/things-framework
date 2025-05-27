package cn.huangdayu.things.camel;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.spi.RouteController;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import java.util.Map;

/**
 * @author huangdayu
 */
@Slf4j
@AllArgsConstructor
public class ThingsMqttReconnectCallback implements MqttCallback {

    private CamelContext context;
    private MqttClient client;
    private Map<String, String> routeIdMap;
    private MqttConnectionOptions options;

    @SneakyThrows
    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {
        log.warn("MqttClient [{}/{}] disconnected to reconnect", client.getServerURI(), client.getClientId());
    }

    @Override
    public void mqttErrorOccurred(MqttException exception) {
        try {
            log.warn("MqttClient [{}/{}] exception to reconnect", client.getServerURI(), client.getClientId(), exception);
            if (MqttClientException.REASON_CODE_CLIENT_NOT_CONNECTED == exception.getReasonCode()) {
                if (!client.isConnected()) {
                    client.reconnect();
                }
            }
        } catch (Exception e) {
            log.error("SofaBus MqttClient [{}/{}] connect error : {}",
                    client.getServerURI(), client.getClientId(), e.getMessage());
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttToken token) {

    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        if (reconnect && options.isCleanStart()) {
            // 停止并重启路由
            RouteController routeController = context.getRouteController();
            routeIdMap.values().forEach(routeId -> {
                try {
                    routeController.stopRoute(routeId);
                    routeController.startRoute(routeId);
                } catch (Exception e) {
                    log.warn("MqttClient [{}/{}] connectComplete , route [{}] restart route error", client.getServerURI(), client.getClientId(), routeId, e);
                }
            });
        }
    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {

    }
}
