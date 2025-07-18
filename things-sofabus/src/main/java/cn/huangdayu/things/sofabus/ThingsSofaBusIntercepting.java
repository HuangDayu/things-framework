package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.api.message.ThingsIntercepting;
import cn.huangdayu.things.common.annotation.ThingsInterceptor;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static cn.huangdayu.things.common.constants.ThingsConstants.MethodActions.THINGS_ACTION_REQUEST;
import static cn.huangdayu.things.common.constants.ThingsConstants.MethodActions.THINGS_ACTION_RESPONSE;
import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.THINGS_SERVICE_REQUEST;
import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_DOT;
import static cn.huangdayu.things.common.enums.ThingsChainingType.OUTPUTTING;
import static cn.huangdayu.things.common.utils.ThingsUtils.equalsThings;

/**
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
@ThingsInterceptor(chainingType = OUTPUTTING)
public class ThingsSofaBusIntercepting implements ThingsIntercepting {

    private final ThingsSofaBusManager thingsSofaBusManager;

    /**
     * 发送完成后，如果没有订阅request对应的response则订阅
     *
     * @param thingsRequest
     * @param thingsResponse
     * @param exception
     */
    @Override
    public void afterCompletion(ThingsRequest thingsRequest, ThingsResponse thingsResponse, Exception exception) {
        ThingsRequestMessage trm = thingsRequest.getTrm();
        if (equalsThings(THINGS_SERVICE_REQUEST, trm.getMethod())) {
            String method = trm.getMethod().replace(THINGS_DOT + THINGS_ACTION_REQUEST, THINGS_DOT + THINGS_ACTION_RESPONSE);
            thingsSofaBusManager.subscribe(thingsRequest.getSubscriber(), false, trm.getMessageMethod().getProductCode(), trm.getMessageMethod().getDeviceCode(), method);
        }
    }
}
