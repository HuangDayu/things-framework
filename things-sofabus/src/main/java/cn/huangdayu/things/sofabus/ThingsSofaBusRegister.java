package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.api.infrastructure.ThingsConfigurator;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusDescriber;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusPublisher;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.events.ThingsContainerRegisteredEvent;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.THINGS_IDENTIFIER;
import static cn.huangdayu.things.common.constants.ThingsConstants.SystemMethod.*;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsSofaBusRegister {
    private final ThingsSofaBusDescriber thingsSofaBusDescriber;
    private final ThingsConfigurator thingsConfigurator;
    private final ThingsSofaBusPublisher thingsSofaBusPublisher;
    private final ThingsEventObserver thingsEventObserver;

    @PostConstruct
    public void initListener() {
        thingsEventObserver.registerObserver(ThingsContainerRegisteredEvent.class, event -> registerDSL());
    }

    private void registerDSL() {
        ThingsRequest thingsRequest = new ThingsRequest();
        thingsRequest.setJtm(JsonThingsMessage.builder()
                .qos(2)
                .metadata(JSONObject.from(new BaseThingsMetadata(SYSTEM_METHOD_TOPIC, thingsConfigurator.getProperties().getCode())))
                .payload(JSONObject.from(thingsSofaBusDescriber.getDSL()))
                .method(THINGS_SYSTEM_POST.replace(THINGS_IDENTIFIER, SYSTEM_METHOD_DSL))
                .build());
        thingsSofaBusPublisher.output(thingsRequest, new ThingsResponse());
    }
}
