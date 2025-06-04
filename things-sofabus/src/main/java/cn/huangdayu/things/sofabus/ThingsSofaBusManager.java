package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.api.container.ThingsDescriber;
import cn.huangdayu.things.api.infrastructure.ThingsConfigurator;
import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusCreator;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusInputting;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.events.ThingsContainerUpdatedEvent;
import cn.huangdayu.things.common.events.ThingsSessionUpdatedEvent;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.properties.ThingsEngineProperties;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.common.wrapper.ThingsSession;
import cn.huangdayu.things.common.wrapper.ThingsSubscribes;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.ERROR;
import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.*;
import static cn.huangdayu.things.common.constants.ThingsConstants.SystemMethod.*;
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

    private final ThingsSofaBusInputting thingsSofaBusInputting;
    private final ThingsConfigurator thingsConfigurator;
    private final ThingsEventObserver thingsEventObserver;
    private final ThingsDescriber thingsDescriber;
    private final Map<String, ThingsSofaBusCreator> thingsBusComponentCreators;
    private static final Map<ThingsEngineProperties.ThingsSofaBusProperties, ThingsSofaBus> propertiesComponentsMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        initSofaBus();
        initListener();
    }

    @PreDestroy
    private void stopSofaBus() {
        propertiesComponentsMap.forEach((key, value) -> value.stop());
    }


    private ThingsSofaBus constructSofaBus(ThingsEngineProperties.ThingsSofaBusProperties property, ThingsSofaBusInputting thingsSofaBusInputting) {
        return propertiesComponentsMap.computeIfAbsent(property, k -> {
            for (Map.Entry<String, ThingsSofaBusCreator> entry : thingsBusComponentCreators.entrySet()) {
                if (entry.getValue().supports().contains(property.getType())) {
                    return entry.getValue().create(property, thingsSofaBusInputting);
                }
            }
            throw new ThingsException(ERROR, "Not found supports Things sofaBus for " + property.getTopic() + ".");
        });
    }


    private void initSofaBus() {
        Set<ThingsEngineProperties.ThingsSofaBusProperties> sofaBus = thingsConfigurator.getProperties().getSofaBus();
        if (CollUtil.isNotEmpty(sofaBus)) {
            for (ThingsEngineProperties.ThingsSofaBusProperties sofaBusProperties : sofaBus) {
                if (!sofaBusProperties.isEnable()) {
                    break;
                }
                ThingsSofaBus thingsSofaBus = constructSofaBus(sofaBusProperties, thingsSofaBusInputting);
                thingsSofaBus.start();
                log.info("Things sofaBus init success, type: {} , server: {}", sofaBusProperties.getType(), sofaBusProperties.getServer());
            }
        }
    }

    private void initListener() {
        thingsEventObserver.registerObserver(ThingsSessionUpdatedEvent.class, event -> initSessionSubscribe(event.getSession()));
        thingsEventObserver.registerObserver(ThingsContainerUpdatedEvent.class, event -> {
            initDslSubscribe();
            initSystemSubscribe();
            dslPost();
        });
    }

    private void dslPost() {
        ThingsRequest thingsRequest = new ThingsRequest();
        thingsRequest.setJtm(JsonThingsMessage.builder()
                .qos(2)
                .metadata(JSONObject.from(new BaseThingsMetadata(SYSTEM_METHOD_TOPIC, thingsConfigurator.getProperties().getCode())))
                .payload(JSONObject.from(thingsDescriber.getDsl()))
                .method(THINGS_SYSTEM_POST.replace(THINGS_IDENTIFIER, SYSTEM_METHOD_DSL))
                .build());
        output(thingsRequest, new ThingsResponse());
    }

    private void initSystemSubscribe() {
        subscribe(false, SYSTEM_METHOD_TOPIC, thingsConfigurator.getProperties().getCode(), THINGS_WILDCARD);
    }

    private void initDslSubscribe() {
        thingsDescriber.getDsl().getThingsDsl().forEach(thingsInfo -> {
            String code = thingsInfo.getProfile().getProduct().getCode();
            subscribe(true, code, null, null);
        });
        thingsDescriber.getDsl().getDomainDsl().forEach(domainInfo -> {
            domainInfo.getSubscribes().forEach(info -> {
                subscribe(false, info.getProductCode(), null, THINGS_EVENT_POST.replace(THINGS_IDENTIFIER, info.getEventIdentifier()));
            });
            domainInfo.getConsumes().forEach(info -> {
                subscribe(false, info.getProductCode(), null, THINGS_SERVICE_RESPONSE.replace(THINGS_IDENTIFIER, info.getServiceIdentifier()));
            });
        });
    }

    private void initSessionSubscribe(ThingsSession session) {
        if (session != null) {
            if (session.isOnline()) {
                subscribe(false, session.getProductCode(), session.getDeviceCode(), null);
            } else {
                unsubscribe(false, session.getProductCode(), session.getDeviceCode(), null);
            }
        }
    }

    /**
     * 输出消息，如果指定目标SofaBus则指定输出，否则全部多sofaBus都输出
     *
     * @param thingsRequest
     * @param thingsResponse
     */
    public void output(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        if (thingsRequest.getTarget() instanceof ThingsSofaBus thingsSofaBus) {
            thingsSofaBus.output(thingsRequest, thingsResponse);
        } else {
            Set<ThingsSofaBus> thingsSofaBus = getAllSofaBus();
            for (ThingsSofaBus bus : thingsSofaBus) {
                if (bus.isStarted()) {
                    bus.output(thingsRequest, thingsResponse);
                }
            }
        }
    }

    public boolean destroy(ThingsEngineProperties.ThingsSofaBusProperties property) {
        ThingsSofaBus thingsSofaBus = propertiesComponentsMap.get(property);
        if (thingsSofaBus != null) {
            return thingsSofaBus.stop();
        }
        return false;
    }

    public Set<ThingsSofaBus> getAllSofaBus() {
        return new HashSet<>(propertiesComponentsMap.values());
    }

    public void subscribe(boolean share, String productCode, String deviceCode, String method) {
        Set<ThingsSofaBus> allSofaBus = getAllSofaBus();
        if (CollUtil.isNotEmpty(allSofaBus)) {
            ThingsSubscribes thingsSubscribes = new ThingsSubscribes();
            thingsSubscribes.setShare(share);
            thingsSubscribes.setProductCode(productCode);
            thingsSubscribes.setDeviceCode(deviceCode);
            thingsSubscribes.setMethod(method);
            allSofaBus.forEach(thingsSofaBus -> thingsSofaBus.subscribe(thingsSubscribes));
        }
    }

    public void unsubscribe(boolean share, String productCode, String deviceCode, String method) {
        Set<ThingsSofaBus> allSofaBus = getAllSofaBus();
        if (CollUtil.isNotEmpty(allSofaBus)) {
            ThingsSubscribes thingsSubscribes = new ThingsSubscribes();
            thingsSubscribes.setShare(share);
            thingsSubscribes.setProductCode(productCode);
            thingsSubscribes.setDeviceCode(deviceCode);
            thingsSubscribes.setMethod(method);
            allSofaBus.forEach(thingsSofaBus -> thingsSofaBus.unsubscribe(thingsSubscribes));
        }
    }

}
