package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.api.infrastructure.ThingsConfigurator;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusDescriber;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusPublisher;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.events.ThingsContainerRegisteredEvent;
import cn.huangdayu.things.common.message.ThingsMessageMethod;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.THINGS_POST;
import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.THINGS_SYSTEM;
import static cn.huangdayu.things.common.constants.ThingsConstants.SystemMethod.SYSTEM_METHOD_DSL;
import static cn.huangdayu.things.common.constants.ThingsConstants.SystemMethod.SYSTEM_METHOD_TOPIC;

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
        thingsRequest.setTrm(ThingsRequestMessage.builder()
                .qos(2)
                .params(JSONObject.from(thingsSofaBusDescriber.getDSL()))
                .method(new ThingsMessageMethod(SYSTEM_METHOD_TOPIC, thingsConfigurator.getProperties().getCode(), THINGS_SYSTEM, SYSTEM_METHOD_DSL, THINGS_POST).toString())
                .build());
        thingsSofaBusPublisher.output(thingsRequest, new ThingsResponse());
    }
}
