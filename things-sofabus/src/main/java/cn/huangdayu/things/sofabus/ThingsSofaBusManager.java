package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.api.infrastructure.ThingsConfigService;
import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusSubscriber;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.properties.ThingsSofaBusProperties;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsSofaBusManager {

    private final ThingsChaining thingsChaining;
    private final ThingsSofaBusFactory thingsSofaBusFactory;
    private final ThingsConfigService thingsConfigService;
    private final ThingsSofaBusTopics thingsSofaBusTopics;
    private final Map<String, ThingsSofaBusSubscriber> thingsSofaBusSubscriberMap;

    @PostConstruct
    public void init() {
        startSofaBus();
        subscribe();
    }

    @PreDestroy
    private void stopSofaBus() {
        thingsSofaBusFactory.destroyAll();
    }


    private void subscribe() {
        Set<ThingsSofaBus> allSofaBus = thingsSofaBusFactory.getAllSofaBus();
        if (CollUtil.isNotEmpty(allSofaBus)) {
            thingsSofaBusSubscriberMap.forEach((key, subscriber) -> {
                allSofaBus.forEach(thingsSofaBus -> {
                    subscriber.getSubscribes().forEach(subscribe -> {
                        thingsSofaBus.subscribe(thingsSofaBusTopics.getSubscribeTopic(subscribe));
                    });
                });
            });
        }
    }

    private void startSofaBus() {
        Set<ThingsSofaBusProperties> sofaBus = thingsConfigService.getProperties().getSofaBus();
        if (CollUtil.isNotEmpty(sofaBus)) {
            for (ThingsSofaBusProperties sofaBusProperties : sofaBus) {
                if (!sofaBusProperties.isEnable()) {
                    break;
                }
                ThingsSofaBus thingsSofaBus = thingsSofaBusFactory.construct(sofaBusProperties, thingsChaining);
                thingsSofaBus.init();
                thingsSofaBus.start();
            }
        }
    }

}
