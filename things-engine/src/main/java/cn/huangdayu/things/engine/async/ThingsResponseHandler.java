package cn.huangdayu.things.engine.async;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.chaining.handler.ThingsHandler;
import cn.huangdayu.things.engine.exception.ThingsException;
import cn.huangdayu.things.engine.message.JsonThingsMessage;

import static cn.huangdayu.things.engine.common.ThingsConstants.ErrorCodes.BAD_REQUEST;
import static cn.huangdayu.things.engine.common.ThingsUtils.getUUID;

/**
 * @author huangdayu
 */
@ThingsBean(order = 1)
public class ThingsResponseHandler implements ThingsHandler {

    @Override
    public JsonThingsMessage doHandler(JsonThingsMessage jsonThingsMessage) {
        if (jsonThingsMessage.isResponse() && ThingsAsyncManager.asyncResponse(jsonThingsMessage)) {
            return jsonThingsMessage.success();
        }
        throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Can't handler this message.", getUUID());
    }

}
