package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.camel.CamelSofaBusConstructor;
import cn.huangdayu.things.camel.ThingsMqttReconnectCallback;
import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import cn.huangdayu.things.common.properties.ThingsSofaBusProperties;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.common.wrapper.ThingsSubscribes;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.component.ComponentsBuilderFactory;
import org.apache.camel.component.paho.mqtt5.PahoMqtt5Component;
import org.apache.camel.support.DefaultComponent;
import org.eclipse.paho.mqttv5.client.MqttClient;
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

    public MqttSofaBus(CamelSofaBusConstructor constructor) {
        super(constructor);
    }

    @Override
    public ThingsSofaBusType getType() {
        return MQTT;
    }

    @Override
    protected String getTopic(ThingsSubscribes thingsSubscribes) {
        String groupId = constructor.getProperties().getGroupId();
        String baseTopic = thingsSubscribes.isShare() ? "$share/" + (isNotBlank(groupId) ? groupId : thingsSubscribes.getProductCode()) + "/" : "";
        String method = thingsSubscribes.getMethod();
        if (isNotBlank(method) && method.split("\\.").length == 4) {
            String[] split = method.split("\\.");
            method = split[1].concat("/").concat(split[2]).concat("/").concat(split[3]);
        } else {
            method = MULTI_LEVEL_TOPIC_WILDCARD;
        }
        return baseTopic + String.format("things/%s/%s/%s", thingsSubscribes.getProductCode(), thingsSubscribes.getDeviceCode(), method)
                .replaceAll(NULL_VALUE, ONE_LEVEL_TOPIC_WILDCARD)
                .replaceAll(THINGS_WILDCARD, ONE_LEVEL_TOPIC_WILDCARD)
                .concat(thingsSubscribes.getJtm() != null ? "?qos=" + thingsSubscribes.getJtm().getQos() : "");
    }

    @Override
    public boolean output(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        try {
            MqttClient client = ((PahoMqtt5Component) component).getClient();
            if (client != null && !client.isConnected()) {
                client.reconnect();
            }
        } catch (Exception e) {
            log.error("SofaBus MqttClient [{}/{}] connect error : {}",
                    constructor.getProperties().getServer(), constructor.getProperties().getClientId(), e.getMessage());
        }
        return super.output(thingsRequest, thingsResponse);
    }

    @SneakyThrows
    @Override
    public DefaultComponent buildComponent() {
        ThingsSofaBusProperties properties = constructor.getProperties();
        MqttClient client = new MqttClient(properties.getServer(), properties.getClientId(), new MqttDefaultFilePersistence(properties.getPersistenceDir()));
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setUserName(properties.getUserName());
        if (isNotBlank(properties.getPassword())) {
            options.setPassword(properties.getPassword().getBytes(StandardCharsets.UTF_8));
        }
        options.setMaxReconnectDelay(30_000);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(60);
        options.setAutomaticReconnect(true);
        options.setCleanStart(false);
        options.setSessionExpiryInterval(0L);
        client.setCallback(new ThingsMqttReconnectCallback(camelContext, client, ROUTE_ID_MAP, options));
        client.connect(options);
        return ComponentsBuilderFactory.pahoMqtt5()
                .filePersistenceDirectory(properties.getPersistenceDir())
                .client(client)
                .build();
    }
}
