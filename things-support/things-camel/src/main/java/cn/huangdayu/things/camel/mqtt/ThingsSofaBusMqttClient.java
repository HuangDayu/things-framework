package cn.huangdayu.things.camel.mqtt;

import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttClientPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author huangdayu
 */
public class ThingsSofaBusMqttClient extends MqttClient {

    public ThingsSofaBusMqttClient(String serverURI, String clientId) throws MqttException {
        super(serverURI, clientId);
    }

    public ThingsSofaBusMqttClient(String serverURI, String clientId, MqttClientPersistence persistence) throws MqttException {
        super(serverURI, clientId, persistence);
    }

    public ThingsSofaBusMqttClient(String serverURI, String clientId, MqttClientPersistence persistence, ScheduledExecutorService executorService) throws MqttException {
        super(serverURI, clientId, persistence, executorService);
    }

    /**
     * 修复因 PahoMqtt5Consumer 中覆盖了 MqttCallback，且只恢复订阅了一个topic而导致其他route失效的问题，
     * 也导致外面配置的callback失效，而无法重新加载route的问题
     * 所以将所有callback添加到Set中，指定主要的callback实现，遍历执行所有添加进来的callback
     * 总的来说，就是修复了同一个client多个route而无法正常重连后恢复所有订阅的问题
     *
     * @param callback the class to callback when for events related to the client
     */
    @Override
    public void setCallback(MqttCallback callback) {
        ThingsMultiMqttCallback mqtt5Callback = ThingsMultiMqttCallback.getInstance();
        mqtt5Callback.addMqttCallback(callback);
        super.setCallback(mqtt5Callback);
    }
}
