package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.message.ThingsHandler;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.huangdayu.things.engine.core.ThingsInvoker;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.EVENT_LISTENER_START_WITH;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean(order = 2)
public class ThingsInvokerHandler implements ThingsHandler {

    private final ThingsFrameworkProperties thingsFrameworkProperties;
    private final ThingsInvoker thingsInvoker;

    @Override
    public boolean canHandle(JsonThingsMessage jsonThingsMessage) {
        return !jsonThingsMessage.isResponse() && canHandleMessage(jsonThingsMessage);
    }

    @Override
    public JsonThingsMessage syncHandler(JsonThingsMessage jsonThingsMessage) {
        return thingsInvoker.syncInvoker(jsonThingsMessage);
    }

    @Override
    public CompletableFuture<JsonThingsMessage> asyncHandler(JsonThingsMessage jsonThingsMessage) {
        return thingsInvoker.asyncInvoker(jsonThingsMessage);
    }

    private boolean canHandleMessage(JsonThingsMessage jsonThingsMessage) {
        BaseThingsMetadata baseMetadata = jsonThingsMessage.getBaseMetadata();
        if (jsonThingsMessage.getMethod().startsWith(EVENT_LISTENER_START_WITH)) {
            return thingsFrameworkProperties.getInstance().getConsumes().contains(baseMetadata.getProductCode());
        }
        return thingsFrameworkProperties.getInstance().getProvides().contains(baseMetadata.getProductCode());
    }
}
