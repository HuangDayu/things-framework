package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsHandling;
import cn.huangdayu.things.common.annotation.ThingsHandler;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.huangdayu.things.engine.core.ThingsInvoker;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.common.enums.ThingsChainingType.INPUTTING;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsHandler(order = 1, chainingType = INPUTTING)
public class ThingsInvokingHandling implements ThingsHandling {

    private final ThingsInvoker thingsInvoker;


    @Override
    public boolean canHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        return thingsInvoker.canInvoke(thingsRequest.getJtm());
    }

    @Override
    public void doHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        JsonThingsMessage jtm = thingsInvoker.syncInvoke(thingsRequest.getJtm());
        if (jtm != null) {
            thingsResponse.setJtm(jtm);
        }
    }
}
