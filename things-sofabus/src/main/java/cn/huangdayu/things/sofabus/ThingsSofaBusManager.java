package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.api.container.ThingsDescriber;
import cn.huangdayu.things.api.infrastructure.ThingsPropertiesService;
import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusCreator;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.observer.event.ThingsContainerUpdatedEvent;
import cn.huangdayu.things.common.observer.event.ThingsSessionUpdatedEvent;
import cn.huangdayu.things.common.properties.ThingsSofaBusProperties;
import cn.huangdayu.things.common.wrapper.ThingsSession;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.ERROR;

/**
 * topic分级包括【系统级，租户级，应用级，产品级，设备级，指令级，分组级，任务级】
 *
 * $sys/things/${productCode}/${deviceCode}/${method}
 *
 * 设备级topic
 * 事件上报： $sys/things/${productCode}/${deviceCode}/thing.event.identifier.post
 * 服务调用： $sys/things/${productCode}/${deviceCode}/thing.service.identifier.request
 * 服务响应： $sys/things/${productCode}/${deviceCode}/thing.service.identifier.response
 * 属性上报： $sys/things/${productCode}/${deviceCode}/thing.properties.all.post
 * 属性设置： $sys/things/${productCode}/${deviceCode}/thing.properties.all.set
 * 属性查询： $sys/things/${productCode}/${deviceCode}/thing.properties.all.get
 *
 * 系统级topic
 * 模型上报： $sys/things/${productCode}/${deviceCode}/thing.system.model.post
 * 模型查询： $sys/things/${productCode}/${deviceCode}/thing.system.model.get
 * 配置上报： $sys/things/${productCode}/${deviceCode}/thing.system.config.post
 * 配置查询： $sys/things/${productCode}/${deviceCode}/thing.system.config.get
 * 配置设置： $sys/things/${productCode}/${deviceCode}/thing.system.config.set
 *
 * 设备登入： $sys/things/${productCode}/${deviceCode}/thing.system.signIn.post
 * 设备登出： $sys/things/${productCode}/${deviceCode}/thing.system.signOut.post
 *
 * 服务调用（服务主动连接设备）: $sys/things/${productCode}/${clientCode}/thing.service.identifier.request
 * 服务响应（服务主动连接设备）: $sys/things/${productCode}/${clientCode}/thing.service.identifier.response
 *
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
@ThingsBean
public class ThingsSofaBusManager {

    private final ThingsChaining thingsChaining;
    private final ThingsPropertiesService thingsConfigService;
    private final ThingsEventObserver thingsEventObserver;
    private final ThingsDescriber thingsDescriber;
    private final Map<String, ThingsSofaBusCreator> thingsBusComponentCreators;
    private static final Map<ThingsSofaBusProperties, ThingsSofaBus> propertiesComponentsMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        initSofaBus();
        initListener();
    }

    @PreDestroy
    private void stopSofaBus() {
        propertiesComponentsMap.forEach((key, value) -> value.stop());
    }


    private ThingsSofaBus constructSofaBus(ThingsSofaBusProperties property, ThingsChaining thingsChaining) {
        return propertiesComponentsMap.computeIfAbsent(property, k -> {
            for (Map.Entry<String, ThingsSofaBusCreator> entry : thingsBusComponentCreators.entrySet()) {
                if (entry.getValue().supports().contains(property.getType())) {
                    return entry.getValue().create(property, thingsChaining);
                }
            }
            throw new ThingsException(ERROR, "Not found supports Things sofaBus for " + property.getTopic() + ".");
        });
    }


    private void initSofaBus() {
        Set<ThingsSofaBusProperties> sofaBus = thingsConfigService.getProperties().getSofaBus();
        if (CollUtil.isNotEmpty(sofaBus)) {
            for (ThingsSofaBusProperties sofaBusProperties : sofaBus) {
                if (!sofaBusProperties.isEnable()) {
                    break;
                }
                ThingsSofaBus thingsSofaBus = constructSofaBus(sofaBusProperties, thingsChaining);
                thingsSofaBus.init();
                thingsSofaBus.start();
                log.info("Things sofaBus init success, type: {} , server: {}", sofaBusProperties.getType(), sofaBusProperties.getServer());
            }
        }
    }

    private void initListener() {
        thingsEventObserver.registerObserver(ThingsSessionUpdatedEvent.class, event -> {
            ThingsSession session = event.getSession();
            if (session != null) {
                String topic = createTopic(session);
                if (session.isOnline()) {
                    subscribe(topic);
                } else {
                    unsubscribe(topic);
                }
            }
        });
        thingsEventObserver.registerObserver(ThingsContainerUpdatedEvent.class, event -> {
            thingsDescriber.getDsl().getThingsDsl().forEach(thingsInfo -> {
                String code = thingsInfo.getProfile().getProduct().getCode();
                subscribe(createTopic(code, code));
            });
        });
    }

    private void subscribe(String topic) {
        Set<ThingsSofaBus> allSofaBus = getAllSofaBus();
        if (CollUtil.isNotEmpty(allSofaBus)) {
            allSofaBus.forEach(thingsSofaBus -> thingsSofaBus.subscribe(topic));
        }
    }

    private void unsubscribe(String topic) {
        Set<ThingsSofaBus> allSofaBus = getAllSofaBus();
        if (CollUtil.isNotEmpty(allSofaBus)) {
            allSofaBus.forEach(thingsSofaBus -> thingsSofaBus.unsubscribe(topic));
        }
    }

    public boolean destroy(ThingsSofaBusProperties property) {
        ThingsSofaBus thingsSofaBus = propertiesComponentsMap.get(property);
        if (thingsSofaBus != null) {
            return thingsSofaBus.stop();
        }
        return false;
    }

    public Set<ThingsSofaBus> getAllSofaBus() {
        return new HashSet<>(propertiesComponentsMap.values());
    }

    public static String createTopic(String productCode, String clientCode) {
        return String.format("$sys/things/%s/%s/#", productCode, clientCode);
    }

    public static String createTopic(ThingsSession session) {
        return String.format("$sys/things/%s/%s/#", session.getProductCode(), session.getDeviceCode());
    }

    public static String createTopic(JsonThingsMessage jtm) {
        BaseThingsMetadata baseMetadata = jtm.getBaseMetadata();
        return String.format("$sys/things/%s/%s/%s", baseMetadata.getProductCode(), baseMetadata.getDeviceCode(), jtm.getMethod());
    }

}
