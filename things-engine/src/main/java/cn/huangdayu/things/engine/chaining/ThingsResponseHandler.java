package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsHandler;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.async.ThingsAsyncManager;

import static cn.huangdayu.things.common.utils.ThingsUtils.getUUID;

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
