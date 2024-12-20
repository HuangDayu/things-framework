package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsHandling;
import cn.huangdayu.things.common.annotation.ThingsHandler;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;

import static cn.huangdayu.things.common.constants.ThingsConstants.SystemMethod.SYSTEM_METHOD_LOGIN;
import static cn.huangdayu.things.common.enums.ThingsMethodType.SYSTEM;
import static cn.huangdayu.things.common.enums.ThingsStreamingType.INPUTTING;

/**
 * @author huangdayu
 */
@ThingsHandler(source = INPUTTING, method = SYSTEM, identifier = SYSTEM_METHOD_LOGIN)
public class ThingsLoginHandling implements ThingsHandling {
    @Override
    public void doHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {

    }
}
