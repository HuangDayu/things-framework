package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusCallback;
import cn.huangdayu.things.camel.CamelSofaBusConstructor;
import cn.huangdayu.things.camel.mqtt.ThingsCamelMqttCallback;
import cn.huangdayu.things.camel.mqtt.ThingsProxyMqttCallback;
import cn.huangdayu.things.camel.mqtt.ThingsSofaBusMqttCallback;
import cn.huangdayu.things.camel.mqtt.ThingsSofaBusMqttClient;
import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import cn.huangdayu.things.common.message.ThingsMessageMethod;
import cn.huangdayu.things.common.properties.ThingsEngineProperties;
import cn.huangdayu.things.common.wrapper.ThingsSubscribes;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.component.ComponentsBuilderFactory;
import org.apache.camel.support.DefaultComponent;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MqttDefaultFilePersistence;

import java.nio.charset.StandardCharsets;

import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_WILDCARD;
import static cn.huangdayu.things.common.enums.ThingsSofaBusType.MQTT;
import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;

/**
 * @author huangdayu
 */
@Slf4j
@Getter
public class MqttSofaBus extends AbstractSofaBus implements ThingsSofaBus {

    private static final String MULTI_LEVEL_TOPIC_WILDCARD = "#";
    private static final String ONE_LEVEL_TOPIC_WILDCARD = "+";
    private static final String NULL_VALUE = "null";

    private ThingsSofaBusMqttCallback callback;

    public MqttSofaBus(CamelSofaBusConstructor constructor) {
        super(constructor);
    }

    @Override
    public ThingsSofaBusType getType() {
        return MQTT;
    }

    @Override
    protected String createTopic(ThingsSubscribes thingsSubscribes) {
        String groupId = constructor.getProperties().getGroupId();
        ThingsMessageMethod method = thingsSubscribes.getMethod();
        String baseTopic = thingsSubscribes.isShare() ? "$share/" + (isNotBlank(groupId) ? groupId : method.getProductCode()) + "/" : "";
        return baseTopic + String.format("things/%s", method.toString())
                .replaceAll(NULL_VALUE, ONE_LEVEL_TOPIC_WILDCARD)
                .replaceAll(THINGS_WILDCARD, ONE_LEVEL_TOPIC_WILDCARD);
    }

    @SneakyThrows
    @Override
    public DefaultComponent buildComponent() {
        ThingsEngineProperties.ThingsSofaBusProperties properties = constructor.getProperties();
        ThingsSofaBusMqttClient client = new ThingsSofaBusMqttClient(properties.getServer(), properties.getClientId(),
                new MqttDefaultFilePersistence(properties.getPersistenceDir()), new ThingsProxyMqttCallback());
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setUserName(properties.getUserName());
        if (isNotBlank(properties.getPassword())) {
            options.setPassword(properties.getPassword().getBytes(StandardCharsets.UTF_8));
        }
        options.setMaxReconnectDelay(5000);
        options.setConnectionTimeout(5);
        options.setKeepAliveInterval(60);
        options.setAutomaticReconnect(true);
        options.setCleanStart(false);
        options.setSessionExpiryInterval(0L);
        client.setCallback(new ThingsCamelMqttCallback(camelContext, client, this, options));
        client.connect(options);
        this.callback = new ThingsSofaBusMqttCallback(client);
        return ComponentsBuilderFactory.pahoMqtt5()
                .filePersistenceDirectory(properties.getPersistenceDir())
                .client(client)
                .build();
    }

    @Override
    protected ThingsSofaBusCallback getCallback() {
        return callback;
    }
}
