package cn.huangdayu.things.mqtt;

import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class MqttService {

    private final MessageChannel mqttOutboundChannel;
    private final MqttPahoMessageHandler mqttOutbound;

    public MqttService(MessageChannel mqttOutboundChannel, MqttPahoMessageHandler mqttOutbound) {
        this.mqttOutboundChannel = mqttOutboundChannel;
        this.mqttOutbound = mqttOutbound;
    }

    public void subscribe(String topic) {
        // Spring Integration 的 MQTT 支持自动订阅配置的主题
        // 如果需要动态订阅，可以通过 MqttPahoMessageDrivenChannelAdapter 动态添加主题
    }

    public void unsubscribe(String topic) {
        // Spring Integration 的 MQTT 不直接支持取消订阅
        // 可以通过重新连接并指定新的订阅主题列表来实现
    }

    public void publish(String topic, String message) {
        Message<String> mqttMessage = MessageBuilder.withPayload(message)
                .setHeader("mqtt_topic", topic)
                .build();
        mqttOutboundChannel.send(mqttMessage);
    }
}