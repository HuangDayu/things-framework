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
import java.util.concurrent.CompletableFuture;
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


    @SneakyThrows
    @Override
    public JsonThingsMessage syncInvoker(JsonThingsMessage jsonThingsMessage) {
        String method = jsonThingsMessage.getMethod();
        if (method.startsWith(SERVICE_START_WITH)) {
            return invokeService(jsonThingsMessage);
        } else if (method.startsWith(EVENT_LISTENER_START_WITH)) {
            return invokeEventListener(jsonThingsMessage);
        } else if (method.startsWith(PROPERTY_METHOD_START_WITH)) {
            return invokeUpdateProperty(jsonThingsMessage);
        } else {
            throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Can't handler this message.");
        }
    }

    @Override
    public Mono<JsonThingsMessage> asyncInvoker(JsonThingsMessage message) {
        return Mono.just(syncInvoker(message));
    }

    private JsonThingsMessage invokeEventListener(JsonThingsMessage jsonThingsMessage) {
        String method = subIdentifies(jsonThingsMessage.getMethod());
        Set<ThingsFunction> functions = findEventListenerFunction(method, jsonThingsMessage.getBaseMetadata().getProductCode());
        if (CollUtil.isNotEmpty(functions)) {
            return asyncInvokeFunctions(jsonThingsMessage, functions);
        }
        throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Things not found this event listener.");
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


    private JsonThingsMessage invokeUpdateProperty(JsonThingsMessage request) {
        BaseThingsMetadata baseThingsMetadata = request.getBaseMetadata();
        Object propertyBean = thingsProperties.getProperties(baseThingsMetadata.getProductCode(), baseThingsMetadata.getDeviceCode());
        if (propertyBean != null) {
            return invokeUpdateProperty(propertyBean, request, baseThingsMetadata);
        }
        throw new ThingsException(request, BAD_REQUEST, "Things ont found Property entry.");
    }

    private JsonThingsMessage invokeUpdateProperty(Object propertyBean, JsonThingsMessage jsonThingsMessage, BaseThingsMetadata baseThingsMetadata) {
        String method = jsonThingsMessage.getMethod();
        if (method.equals(PROPERTY_SET) || method.equals(PROPERTY_GET)) {
            if (method.equals(PROPERTY_SET)) {
                JSONObject payload = jsonThingsMessage.getPayload();
                for (Map.Entry<String, Object> entry : payload.entrySet()) {
                    ReflectUtil.setFieldValue(propertyBean, entry.getKey(), entry.getValue());
                    asyncInvokeFunctions(jsonThingsMessage, THINGS_PROPERTY_LISTENER_TABLE.get(entry.getKey(), baseThingsMetadata.getProductCode()));
                }
                asyncInvokeFunctions(jsonThingsMessage, THINGS_PROPERTY_LISTENER_TABLE.get(THINGS_WILDCARD, baseThingsMetadata.getProductCode()));
            }
            jsonThingsMessage.setPayload((JSONObject) JSON.toJSON(propertyBean));
            jsonThingsMessage.setMethod(PROPERTY_POST);
            return jsonThingsMessage;
        }
        throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Things not support this service.");
    }

    @SneakyThrows
    private JsonThingsMessage invokeService(JsonThingsMessage jsonThingsMessage) {
        ThingsFunction thingsFunction = THINGS_SERVICES_TABLE.get(jsonThingsMessage.getMethod().replace(SERVICE_START_WITH, ""), jsonThingsMessage.getBaseMetadata().getProductCode());
        if (thingsFunction == null) {
            throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Things not found this service.");
        }
        return syncInvokeFunction(jsonThingsMessage, thingsFunction);
    }

    private JsonThingsMessage asyncInvokeFunctions(JsonThingsMessage jsonThingsMessage, Set<ThingsFunction> thingsFunctions) {
        if (CollUtil.isNotEmpty(thingsFunctions)) {
            for (ThingsFunction function : thingsFunctions) {
                THINGS_EXECUTOR.execute(() -> syncInvokeFunction(jsonThingsMessage, function));
            }
        }
        return jsonThingsMessage.success();
    }

    @SneakyThrows
    private JsonThingsMessage syncInvokeFunction(JsonThingsMessage request, ThingsFunction thingsFunction) {
        Object result = thingsFunction.getMethod().invoke(thingsFunction.getBean(), thingsConverter.args(request, thingsFunction));
        if (result != null) {
            if (result instanceof JsonThingsMessage message) {
                return message;
            } else if (result instanceof Future future) {
                return getFuture(future, request);
            } else {
                return request.success(result);
            }
        }
        return request.success();
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
