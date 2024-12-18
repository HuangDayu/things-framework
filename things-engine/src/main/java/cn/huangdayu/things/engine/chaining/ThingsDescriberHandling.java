package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsHandling;
import cn.huangdayu.things.common.annotation.ThingsHandler;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.engine.core.ThingsDescriber;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.common.constants.ThingsConstants.SystemMethod.SYSTEM_METHOD_DSL;
import static cn.huangdayu.things.common.enums.ThingsMethodType.SYSTEM;
import static cn.huangdayu.things.common.enums.ThingsStreamingType.INPUTTING;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsHandler(source = INPUTTING, method = SYSTEM, identifier = SYSTEM_METHOD_DSL)
public class ThingsDescriberHandling implements ThingsHandling {

    private final ThingsDescriber thingsDescriber;

    @Override
    public void doHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        JsonThingsMessage jtm = thingsRequest.getJtm().cloneMessage();
        jtm.setPayload((JSONObject) JSON.toJSON(thingsDescriber.getDsl()));
        thingsResponse.setJtm(jtm);
    }
}
