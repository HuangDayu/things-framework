package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsHandling;
import cn.huangdayu.things.common.annotation.ThingsHandler;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;

import static cn.huangdayu.things.common.constants.ThingsConstants.SystemMethod.SYSTEM_METHOD_LOGOUT;
import static cn.huangdayu.things.common.enums.ThingsMethodType.SYSTEM;
import static cn.huangdayu.things.common.enums.ThingsChainingType.INPUTTING;

/**
 * @author huangdayu
 */
@ThingsHandler(chainingType = INPUTTING, method = SYSTEM, identifier = SYSTEM_METHOD_LOGOUT)
public class ThingsLogoutHandling implements ThingsHandling {
    @Override
    public void doHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {

    }
}
