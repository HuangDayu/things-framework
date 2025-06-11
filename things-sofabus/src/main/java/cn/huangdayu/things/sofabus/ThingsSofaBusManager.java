package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.api.infrastructure.ThingsConfigurator;
import cn.huangdayu.things.api.message.ThingsSubscriber;
import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusCreator;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusSubscriber;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.events.ThingsContainerCancelledEvent;
import cn.huangdayu.things.common.events.ThingsContainerRegisteredEvent;
import cn.huangdayu.things.common.events.ThingsSessionUpdatedEvent;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.properties.ThingsEngineProperties;
import cn.huangdayu.things.common.wrapper.ThingsSession;
import cn.huangdayu.things.common.wrapper.ThingsSubscribes;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.ERROR;
import static cn.huangdayu.things.common.constants.ThingsConstants.SystemMethod.SYSTEM_METHOD_TOPIC;
import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_WILDCARD;

/**
 * topic分级包括【系统级，租户级，应用级，产品级，设备级，指令级，分组级，任务级】
 * <p>
 * $sys/things/${productCode}/${deviceCode}/${method}
 * <p>
 * 设备级topic
 * 事件上报： $sys/things/${productCode}/${deviceCode}/thing.event.identifier.post
 * 服务调用： $sys/things/${productCode}/${deviceCode}/thing.service.identifier.request
 * 服务响应： $sys/things/${productCode}/${deviceCode}/thing.service.identifier.response
 * 属性上报： $sys/things/${productCode}/${deviceCode}/thing.properties.all.post
 * 属性设置： $sys/things/${productCode}/${deviceCode}/thing.properties.all.set
 * 属性查询： $sys/things/${productCode}/${deviceCode}/thing.properties.all.get
 * <p>
 * 系统级topic
 * 模型上报： $sys/things/${productCode}/${deviceCode}/thing.system.model.post
 * 模型查询： $sys/things/${productCode}/${deviceCode}/thing.system.model.get
 * 配置上报： $sys/things/${productCode}/${deviceCode}/thing.system.config.post
 * 配置查询： $sys/things/${productCode}/${deviceCode}/thing.system.config.get
 * 配置设置： $sys/things/${productCode}/${deviceCode}/thing.system.config.set
 * <p>
 * 设备登入： $sys/things/${productCode}/${deviceCode}/thing.system.signIn.post
 * 设备登出： $sys/things/${productCode}/${deviceCode}/thing.system.signOut.post
 * <p>
 * 服务调用（服务主动连接设备）: $sys/things/${productCode}/${clientCode}/thing.service.identifier.request
 * 服务响应（服务主动连接设备）: $sys/things/${productCode}/${clientCode}/thing.service.identifier.response
 *
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
@ThingsBean
public class ThingsSofaBusManager {

    private final ThingsEventObserver thingsEventObserver;
    private final ThingsSofaBusSubscriber thingsSofaBusSubscriber;
    private final ThingsConfigurator thingsConfigurator;
    private final Map<String, ThingsSofaBusCreator> thingsBusComponentCreators;
    private static final Map<ThingsEngineProperties.ThingsSofaBusProperties, ThingsSofaBus> propertiesComponentsMap = new ConcurrentHashMap<>();
    private static final Set<ThingsSubscribes> DSL_SUBSCRIBES = new CopyOnWriteArraySet<>();

    @PostConstruct
    public void init() {
        initSofaBus();
        initListener();
    }

    @PreDestroy
    private void destroy() {
        propertiesComponentsMap.forEach((key, value) -> value.stop());
    }

    public boolean destroy(ThingsEngineProperties.ThingsSofaBusProperties property) {
        ThingsSofaBus thingsSofaBus = propertiesComponentsMap.get(property);
        if (thingsSofaBus != null) {
            return thingsSofaBus.stop();
        }
        return false;
    }

    private void initListener() {
        thingsEventObserver.registerObserver(ThingsSessionUpdatedEvent.class, event -> handleSessionSubscribe(event.getSession()));
        thingsEventObserver.registerObserver(ThingsContainerRegisteredEvent.class, event -> {
            handleDslSubscribe();
            handleSystemSubscribe();
        });
        thingsEventObserver.registerObserver(ThingsContainerCancelledEvent.class, event -> handleDslSubscribe());
    }


    private void initSofaBus() {
        Set<ThingsEngineProperties.ThingsSofaBusProperties> sofaBus = thingsConfigurator.getProperties().getSofaBus();
        if (CollUtil.isNotEmpty(sofaBus)) {
            for (ThingsEngineProperties.ThingsSofaBusProperties sofaBusProperties : sofaBus) {
                if (!sofaBusProperties.isEnable()) {
                    break;
                }
                ThingsSofaBus thingsSofaBus = constructSofaBus(sofaBusProperties);
                thingsSofaBus.start();
                log.info("Things sofaBus init success, type: {} , server: {}", sofaBusProperties.getType(), sofaBusProperties.getServer());
            }
        }
    }

    private ThingsSofaBus constructSofaBus(ThingsEngineProperties.ThingsSofaBusProperties property) {
        return propertiesComponentsMap.computeIfAbsent(property, k -> {
            for (Map.Entry<String, ThingsSofaBusCreator> entry : thingsBusComponentCreators.entrySet()) {
                if (entry.getValue().supports().contains(property.getType())) {
                    return entry.getValue().create(property);
                }
            }
            throw new ThingsException(ERROR, "Not found supports Things sofaBus for " + property.getTopic() + ".");
        });
    }

    public Set<ThingsSofaBus> getAllSofaBus() {
        return new HashSet<>(propertiesComponentsMap.values());
    }

    public void subscribe(Object subscriber, boolean share, String productCode, String deviceCode, String method) {
        ThingsSubscribes thingsSubscribes = new ThingsSubscribes();
        thingsSubscribes.setSubscriber(subscriber);
        thingsSubscribes.setShare(share);
        thingsSubscribes.setProductCode(productCode);
        thingsSubscribes.setDeviceCode(deviceCode);
        thingsSubscribes.setMethod(method);
        ThingsSubscriber thingsSubscriber = thingsSofaBusSubscriber.create(thingsSubscribes);
        subscribe(thingsSubscribes, thingsSubscriber);
    }

    public void subscribe(ThingsSubscribes thingsSubscribes, ThingsSubscriber thingsSubscriber) {
        Set<ThingsSofaBus> allSofaBus = getAllSofaBus();
        if (CollUtil.isNotEmpty(allSofaBus)) {
            allSofaBus.forEach(thingsSofaBus -> thingsSofaBus.subscribe(thingsSubscribes, thingsSubscriber));
        }
    }

    public void unsubscribe(boolean share, String productCode, String deviceCode, String method) {
        ThingsSubscribes thingsSubscribes = new ThingsSubscribes();
        thingsSubscribes.setShare(share);
        thingsSubscribes.setProductCode(productCode);
        thingsSubscribes.setDeviceCode(deviceCode);
        thingsSubscribes.setMethod(method);
        unsubscribe(thingsSubscribes);
    }

    public void unsubscribe(ThingsSubscribes thingsSubscribes) {
        Set<ThingsSofaBus> allSofaBus = getAllSofaBus();
        if (CollUtil.isNotEmpty(allSofaBus)) {
            allSofaBus.forEach(thingsSofaBus -> thingsSofaBus.unsubscribe(thingsSubscribes));
        }
    }

    private void handleSessionSubscribe(ThingsSession session) {
        if (session != null) {
            if (session.isOnline()) {
                subscribe(session, false, session.getProductCode(), session.getDeviceCode(), null);
            } else {
                unsubscribe(false, session.getProductCode(), session.getDeviceCode(), null);
            }
        }
    }

    private void handleDslSubscribe() {
        Set<ThingsSubscribes> dslSubscribes = thingsSofaBusSubscriber.getDslSubscribes();

        dslSubscribes.stream()
                .filter(e -> !DSL_SUBSCRIBES.contains(e))
                .forEach(e -> subscribe(e, thingsSofaBusSubscriber.create(e)));

        DSL_SUBSCRIBES.stream()
                .filter(e -> !dslSubscribes.contains(e))
                .forEach(this::unsubscribe);

        DSL_SUBSCRIBES.clear();
        DSL_SUBSCRIBES.addAll(dslSubscribes);
    }

    private void handleSystemSubscribe() {
        subscribe(this, false, SYSTEM_METHOD_TOPIC, thingsConfigurator.getProperties().getCode(), THINGS_WILDCARD);
    }

}
