package cn.huangdayu.things.camel.mqtt;

import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttClientPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;

import java.util.concurrent.ScheduledExecutorService;

/**
 * 自定义MQTT客户端，支持配置多个不重复Callback对象
 *
 * @author huangdayu
 */
public class ThingsSofaBusMqttClient extends MqttClient {
    private final ThingsProxyMqttCallback proxyMqttCallback;

    public ThingsSofaBusMqttClient(String serverURI, String clientId, ThingsProxyMqttCallback proxyMqttCallback) throws MqttException {
        super(serverURI, clientId);
        this.proxyMqttCallback = proxyMqttCallback;
    }

    public ThingsSofaBusMqttClient(String serverURI, String clientId, MqttClientPersistence persistence, ThingsProxyMqttCallback proxyMqttCallback) throws MqttException {
        super(serverURI, clientId, persistence);
        this.proxyMqttCallback = proxyMqttCallback;
    }

    public ThingsSofaBusMqttClient(String serverURI, String clientId, MqttClientPersistence persistence, ScheduledExecutorService executorService, ThingsProxyMqttCallback proxyMqttCallback) throws MqttException {
        super(serverURI, clientId, persistence, executorService);
        this.proxyMqttCallback = proxyMqttCallback;
    }


    @Override
    public void setCallback(MqttCallback callback) {
        proxyMqttCallback.addMqttCallback(callback);
        super.setCallback(proxyMqttCallback);
    }
}
