package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.api.infrastructure.ThingsConfigService;
import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.sofabus.ThingsSofaBus;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.properties.ThingsSofaBusProperties;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

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

    @PostConstruct
    public void init() {
        Set<ThingsSofaBusProperties> sofaBus = thingsConfigService.getProperties().getSofaBus();
        if (CollUtil.isNotEmpty(sofaBus)) {
            Set<String> subscribeTopicCodes = thingsSofaBusTopics.getSubscribeTopicCodes();
            for (ThingsSofaBusProperties sofaBusProperties : sofaBus) {
                if (!sofaBusProperties.isEnable()) {
                    break;
                }
                ThingsSofaBus thingsSofaBus = thingsSofaBusFactory.create(sofaBusProperties, thingsChaining);
                thingsSofaBus.init();
                thingsSofaBus.start();
                for (String subscribeTopicCode : subscribeTopicCodes) {
                    thingsSofaBus.subscribe(subscribeTopicCode);
                }
            }
        }
    }
}
