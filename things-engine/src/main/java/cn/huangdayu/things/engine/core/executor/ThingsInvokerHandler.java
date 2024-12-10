package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.message.ThingsHandler;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.engine.core.ThingsInvoker;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.EVENT_LISTENER_START_WITH;
import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.PROPERTY_METHOD_START_WITH;
import static cn.huangdayu.things.engine.core.executor.ThingsBaseExecutor.*;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean(order = 2)
public class ThingsInvokerHandler implements ThingsHandler {

    private final ThingsInvoker thingsInvoker;

    @Override
    public boolean canHandle(JsonThingsMessage jtm) {
        BaseThingsMetadata baseMetadata = jtm.getBaseMetadata();
        if (jtm.getMethod().startsWith(EVENT_LISTENER_START_WITH)) {
            return THINGS_EVENTS_LISTENER_TABLE.containsColumn(baseMetadata.getProductCode());
        }
        if (jtm.getMethod().startsWith(PROPERTY_METHOD_START_WITH)) {
            return PRODUCT_PROPERTY_MAP.containsKey(baseMetadata.getProductCode()) || DEVICE_PROPERTY_MAP.containsColumn(baseMetadata.getDeviceCode());
        }
        return THINGS_SERVICES_TABLE.containsColumn(baseMetadata.getProductCode());
    }

    @Override
    public JsonThingsMessage syncHandler(JsonThingsMessage jtm) {
        return thingsInvoker.syncInvoker(jtm);
    }

    @Override
    public Mono<JsonThingsMessage> reactorHandler(JsonThingsMessage jtm) {
        return thingsInvoker.reactorInvoker(jtm);
    }
}
