package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.engine.core.ThingsConverter;
import cn.huangdayu.things.engine.core.ThingsInvoker;
import cn.huangdayu.things.engine.core.ThingsProperties;
import cn.huangdayu.things.engine.wrapper.ThingsFunction;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.BAD_REQUEST;
import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.*;
import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_WILDCARD;
import static cn.huangdayu.things.common.factory.ThreadPoolFactory.THINGS_EXECUTOR;
import static cn.huangdayu.things.common.utils.ThingsUtils.subIdentifies;

/**
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
@ThingsBean
public class ThingsInvokerExecutor extends ThingsBaseExecutor implements ThingsInvoker {

    private final ThingsProperties thingsProperties;
    private final ThingsConverter thingsConverter;

    @Override
    public boolean canInvoke(JsonThingsMessage jtm) {
        BaseThingsMetadata baseMetadata = jtm.getBaseMetadata();
        if (jtm.getMethod().startsWith(EVENT_LISTENER_START_WITH)) {
            return THINGS_EVENTS_LISTENER_TABLE.containsColumn(baseMetadata.getProductCode());
        }
        if (jtm.getMethod().startsWith(PROPERTY_METHOD_START_WITH)) {
            return PRODUCT_PROPERTY_MAP.containsKey(baseMetadata.getProductCode()) || DEVICE_PROPERTY_MAP.containsColumn(baseMetadata.getDeviceCode());
        }
        return THINGS_SERVICES_TABLE.containsColumn(baseMetadata.getProductCode());
    }

    @SneakyThrows
    @Override
    public JsonThingsMessage syncInvoke(JsonThingsMessage jtm) {
        String method = jtm.getMethod();
        if (method.startsWith(SERVICE_START_WITH)) {
            return invokeService(jtm);
        } else if (method.startsWith(EVENT_LISTENER_START_WITH)) {
            return invokeEventListener(jtm);
        } else if (method.startsWith(PROPERTY_METHOD_START_WITH)) {
            return invokeUpdateProperty(jtm);
        } else {
            throw new ThingsException(jtm, BAD_REQUEST, "Can't handler this message.");
        }
    }

    @Override
    public Mono<JsonThingsMessage> reactorInvoke(JsonThingsMessage jtm) {
        return Mono.just(syncInvoke(jtm));
    }

    private JsonThingsMessage invokeEventListener(JsonThingsMessage jtm) {
        String method = subIdentifies(jtm.getMethod());
        Set<ThingsFunction> functions = findEventListenerFunction(method, jtm.getBaseMetadata().getProductCode());
        if (CollUtil.isNotEmpty(functions)) {
            return asyncInvokeFunctions(jtm, functions);
        }
        throw new ThingsException(jtm, BAD_REQUEST, "Things not found this event listener.");
    }


    /**
     * 通配符支持所有产品的同一个事件，一个产品的所有事件，一个产品的某个事件
     *
     * @param identifies
     * @param productCode
     * @return
     */
    private Set<ThingsFunction> findEventListenerFunction(String identifies, String productCode) {
        Set<ThingsFunction> functions = new ConcurrentHashSet<>();
        Set<ThingsFunction> thingsFunctions = THINGS_EVENTS_LISTENER_TABLE.get(identifies, productCode);
        if (CollUtil.isNotEmpty(thingsFunctions)) {
            functions.addAll(thingsFunctions);
        }
        Set<ThingsFunction> thingsFunctions1 = THINGS_EVENTS_LISTENER_TABLE.get(identifies, THINGS_WILDCARD);
        if (CollUtil.isNotEmpty(thingsFunctions1)) {
            functions.addAll(thingsFunctions1);
        }
        Set<ThingsFunction> thingsFunctions2 = THINGS_EVENTS_LISTENER_TABLE.get(THINGS_WILDCARD, productCode);
        if (CollUtil.isNotEmpty(thingsFunctions2)) {
            functions.addAll(thingsFunctions2);
        }
        return functions;
    }


    private JsonThingsMessage invokeUpdateProperty(JsonThingsMessage jtm) {
        BaseThingsMetadata baseThingsMetadata = jtm.getBaseMetadata();
        Object propertyBean = thingsProperties.getProperties(baseThingsMetadata.getProductCode(), baseThingsMetadata.getDeviceCode());
        if (propertyBean != null) {
            return invokeUpdateProperty(propertyBean, jtm, baseThingsMetadata);
        }
        throw new ThingsException(jtm, BAD_REQUEST, "Things ont found Property entry.");
    }

    private JsonThingsMessage invokeUpdateProperty(Object propertyBean, JsonThingsMessage jtm, BaseThingsMetadata baseThingsMetadata) {
        String method = jtm.getMethod();
        if (method.equals(PROPERTY_SET) || method.equals(PROPERTY_GET)) {
            if (method.equals(PROPERTY_SET)) {
                JSONObject payload = jtm.getPayload();
                for (Map.Entry<String, Object> entry : payload.entrySet()) {
                    ReflectUtil.setFieldValue(propertyBean, entry.getKey(), entry.getValue());
                    asyncInvokeFunctions(jtm, THINGS_PROPERTY_LISTENER_TABLE.get(entry.getKey(), baseThingsMetadata.getProductCode()));
                }
                asyncInvokeFunctions(jtm, THINGS_PROPERTY_LISTENER_TABLE.get(THINGS_WILDCARD, baseThingsMetadata.getProductCode()));
            }
            jtm.setPayload((JSONObject) JSON.toJSON(propertyBean));
            jtm.setMethod(PROPERTY_POST);
            return jtm;
        }
        throw new ThingsException(jtm, BAD_REQUEST, "Things not support this service.");
    }

    @SneakyThrows
    private JsonThingsMessage invokeService(JsonThingsMessage jtm) {
        ThingsFunction thingsFunction = THINGS_SERVICES_TABLE.get(jtm.getMethod().replace(SERVICE_START_WITH, ""), jtm.getBaseMetadata().getProductCode());
        if (thingsFunction == null) {
            throw new ThingsException(jtm, BAD_REQUEST, "Things not found this service.");
        }
        return syncInvokeFunction(jtm, thingsFunction);
    }

    private JsonThingsMessage asyncInvokeFunctions(JsonThingsMessage jtm, Set<ThingsFunction> thingsFunctions) {
        if (CollUtil.isNotEmpty(thingsFunctions)) {
            for (ThingsFunction function : thingsFunctions) {
                THINGS_EXECUTOR.execute(() -> syncInvokeFunction(jtm, function));
            }
        }
        return jtm.success();
    }

    @SneakyThrows
    private JsonThingsMessage syncInvokeFunction(JsonThingsMessage jtm, ThingsFunction thingsFunction) {
        Object result = thingsFunction.getMethod().invoke(thingsFunction.getBean(), thingsConverter.args(jtm, thingsFunction));
        if (result != null) {
            if (result instanceof JsonThingsMessage) {
                return (JsonThingsMessage) result;
            } else if (result instanceof Future future) {
                return getFuture(future, jtm);
            } else {
                return jtm.success(result);
            }
        }
        return jtm.success();
    }

    @SneakyThrows
    private JsonThingsMessage getFuture(Future future, JsonThingsMessage request) {
        Object result = future.get(request.getTimeout(), TimeUnit.MILLISECONDS);
        if (result instanceof JsonThingsMessage) {
            return (JsonThingsMessage) result;
        }
        return request.success(result);
    }

}
