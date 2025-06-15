package cn.huangdayu.things.camel.mqtt;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * Callback代理类，避免因 PahoMqtt5Consumer 中配置了callback导致业务中配置的callback无效的问题，同一个callback类的实例只能被添加一次
 *
 * @author huangdayu
 */
public class ThingsProxyMqttCallback implements MqttCallback {

    /**
     * 一个callback实现类只保留一个对象
     */
    private final Set<MqttCallback> MQTT_CALLBACK_SET = new HashSet<>() {
        @Override
        public boolean add(MqttCallback mqttCallback) {
            if (mqttCallback == null) {
                return false;
            }
            removeIf(callback -> callback.getClass().equals(mqttCallback.getClass()));
            return super.add(mqttCallback);
        }
    };

    public void addMqttCallback(MqttCallback mqttCallback) {
        MQTT_CALLBACK_SET.add(mqttCallback);
    }

    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {
        MQTT_CALLBACK_SET.forEach(mqttCallback -> mqttCallback.disconnected(disconnectResponse));
    }

    @Override
    public void mqttErrorOccurred(MqttException exception) {
        MQTT_CALLBACK_SET.forEach(mqttCallback -> mqttCallback.mqttErrorOccurred(exception));
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        for (MqttCallback mqttCallback : MQTT_CALLBACK_SET) {
            mqttCallback.messageArrived(topic, message);
        }
    }

    @Override
    public void deliveryComplete(IMqttToken token) {
        MQTT_CALLBACK_SET.forEach(mqttCallback -> mqttCallback.deliveryComplete(token));
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        MQTT_CALLBACK_SET.forEach(mqttCallback -> mqttCallback.connectComplete(reconnect, serverURI));
    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {
        MQTT_CALLBACK_SET.forEach(mqttCallback -> mqttCallback.authPacketArrived(reasonCode, properties));
    }
}
