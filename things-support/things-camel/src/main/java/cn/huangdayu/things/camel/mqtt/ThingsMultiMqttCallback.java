package cn.huangdayu.things.camel.mqtt;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author huangdayu
 */
public class ThingsMultiMqttCallback implements MqttCallback {

    /**
     * 一个callback实现类只保留一个对象
     */
    private final static Set<MqttCallback> MQTT_CALLBACK_SET = new HashSet<>() {
        @Override
        public boolean add(MqttCallback mqttCallback) {
            if (mqttCallback == null) {
                return false;
            }
            removeIf(callback -> callback.getClass().equals(mqttCallback.getClass()));
            return super.add(mqttCallback);
        }
    };
    private static final ThingsMultiMqttCallback INSTANCE = new ThingsMultiMqttCallback();

    private ThingsMultiMqttCallback() {
    }

    public static ThingsMultiMqttCallback getInstance() {
        return INSTANCE;
    }

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
