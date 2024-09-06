package cn.huangdayu.things.engine.async;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.chaining.handler.ThingsHandler;
import cn.huangdayu.things.engine.message.JsonThingsMessage;

import static cn.huangdayu.things.engine.common.ThingsUtils.getUUID;

/**
 * @author huangdayu
 */
@ThingsBean(order = 1)
public class ThingsResponseHandler implements ThingsHandler {

    @Override
    public boolean canHandle(JsonThingsMessage jsonThingsMessage) {
        return jsonThingsMessage.isResponse() && ThingsAsyncManager.hasAsync(jsonThingsMessage);
    }

    @Override
    public JsonThingsMessage doHandle(JsonThingsMessage jsonThingsMessage) {
        if (jsonThingsMessage.isResponse() && ThingsAsyncManager.asyncResponse(jsonThingsMessage)) {
            return jsonThingsMessage.success();
        }
        return jsonThingsMessage.notFound(getUUID());
    }

}
