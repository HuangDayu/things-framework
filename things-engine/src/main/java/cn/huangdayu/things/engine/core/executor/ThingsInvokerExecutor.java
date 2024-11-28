package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.handler.ThingsHandler;
import cn.huangdayu.things.api.instances.ThingsInstances;
import cn.huangdayu.things.common.annotation.*;
import cn.huangdayu.things.common.event.ThingsAsyncResponseEvent;
import cn.huangdayu.things.common.event.ThingsEventObserver;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.AbstractThingsMessage;
import cn.huangdayu.things.common.message.BaseThingsMessage;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.engine.core.ThingsInvoker;
import cn.huangdayu.things.engine.core.ThingsPropertier;
import cn.huangdayu.things.engine.wrapper.ThingsFunction;
import cn.huangdayu.things.engine.wrapper.ThingsParameter;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.BAD_REQUEST;
import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.*;
import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_WILDCARD;
import static cn.huangdayu.things.common.factory.ThreadPoolFactory.THINGS_EXECUTOR;
import static cn.huangdayu.things.common.utils.ThingsUtils.*;

/**
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
@ThingsBean(order = 2)
public class ThingsInvokerExecutor extends ThingsBaseExecutor implements ThingsInvoker, ThingsHandler {

    private final ThingsPropertier thingsPropertier;
    private final ThingsEventObserver thingsEventObserver;
    private final ThingsInstances thingsInstances;

    private final Map<String, Function<War, Object>> functionMap = Map.of(
            ThingsParam.class.getName(), this::argForThingsParam,
            ThingsMessage.class.getName(), this::argForThingsMessage,
            ThingsPayload.class.getName(), this::argForThingsPayload,
            ThingsMetadata.class.getName(), this::argForThingsMetadata,
            ThingsInject.class.getName(), this::argForThingsInject
    );


    @Override
    public boolean canHandle(JsonThingsMessage jsonThingsMessage) {
        return !jsonThingsMessage.isResponse() && canHandleMessage(jsonThingsMessage);
    }

    @Override
    public JsonThingsMessage doHandle(JsonThingsMessage jsonThingsMessage) {
        return execute(jsonThingsMessage);
    }

    @SneakyThrows
    @Override
    public JsonThingsMessage execute(JsonThingsMessage jsonThingsMessage) {
        String method = jsonThingsMessage.getMethod();
        if (method.startsWith(SERVICE_START_WITH)) {
            return invokeService(jsonThingsMessage);
        } else if (method.startsWith(EVENT_LISTENER_START_WITH)) {
            return invokeEventListener(jsonThingsMessage);
        } else if (method.startsWith(PROPERTY_METHOD_START_WITH)) {
            return updateProperty(jsonThingsMessage);
        } else {
            throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Can't handler this message.");
        }
    }

    private boolean canHandleMessage(JsonThingsMessage jsonThingsMessage) {
        BaseThingsMetadata baseMetadata = jsonThingsMessage.getBaseMetadata();
        return thingsInstances.getThingsInstance().getProvides().contains(baseMetadata.getProductCode()) ||
                thingsInstances.getThingsInstance().getConsumes().contains(baseMetadata.getProductCode());
    }

    private JsonThingsMessage invokeEventListener(JsonThingsMessage jsonThingsMessage) {
        String method = subIdentifies(jsonThingsMessage.getMethod());
        Set<ThingsFunction> functions = getEventListenerFunction(method, jsonThingsMessage.getBaseMetadata().getProductCode());
        if (CollUtil.isNotEmpty(functions)) {
            return tryAsyncInvokeFunctions(jsonThingsMessage, functions);
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
    private Set<ThingsFunction> getEventListenerFunction(String identifies, String productCode) {
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


    public JsonThingsMessage updateProperty(JsonThingsMessage request) {
        BaseThingsMetadata baseThingsMetadata = request.getBaseMetadata();
        Object propertyBean = thingsPropertier.getProperties(baseThingsMetadata.getProductCode(), baseThingsMetadata.getDeviceCode());
        if (propertyBean != null) {
            return updateProperty(propertyBean, request, baseThingsMetadata);
        }
        throw new ThingsException(request, BAD_REQUEST, "Things ont found Property entry.");
    }

    private JsonThingsMessage updateProperty(Object propertyBean, JsonThingsMessage jsonThingsMessage, BaseThingsMetadata baseThingsMetadata) {
        String method = jsonThingsMessage.getMethod();
        if (method.equals(PROPERTY_SET) || method.equals(PROPERTY_GET)) {
            if (method.equals(PROPERTY_SET)) {
                JSONObject payload = jsonThingsMessage.getPayload();
                for (Map.Entry<String, Object> entry : payload.entrySet()) {
                    ReflectUtil.setFieldValue(propertyBean, entry.getKey(), entry.getValue());
                    tryAsyncInvokeFunctions(jsonThingsMessage, THINGS_PROPERTY_LISTENER_TABLE.get(entry.getKey(), baseThingsMetadata.getProductCode()));
                }
                tryAsyncInvokeFunctions(jsonThingsMessage, THINGS_PROPERTY_LISTENER_TABLE.get(THINGS_WILDCARD, baseThingsMetadata.getProductCode()));
            }
            jsonThingsMessage.setPayload((JSONObject) JSON.toJSON(propertyBean));
            jsonThingsMessage.setMethod(PROPERTY_POST);
            return jsonThingsMessage;
        }
        throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Things not support this service.");
    }

    public JsonThingsMessage invokeService(JsonThingsMessage jsonThingsMessage) {
        ThingsFunction thingsFunction = THINGS_SERVICES_TABLE.get(jsonThingsMessage.getMethod().replace(SERVICE_START_WITH, ""), jsonThingsMessage.getBaseMetadata().getProductCode());
        if (thingsFunction == null) {
            throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Things not found this service.");
        }
        if (thingsFunction.isAsync()) {
            tryAsyncInvokeFunction(jsonThingsMessage, thingsFunction);
            return jsonThingsMessage.async();
        }
        return tryInvokeFunction(jsonThingsMessage, thingsFunction);
    }

    private JsonThingsMessage tryAsyncInvokeFunctions(JsonThingsMessage jsonThingsMessage, Set<ThingsFunction> thingsFunctions) {
        if (CollUtil.isNotEmpty(thingsFunctions)) {
            for (ThingsFunction function : thingsFunctions) {
                tryAsyncInvokeFunction(jsonThingsMessage, function);
            }
        }
        return jsonThingsMessage.async();
    }

    private void tryAsyncInvokeFunction(JsonThingsMessage request, ThingsFunction thingsFunction) {
        THINGS_EXECUTOR.execute(() -> {
            JsonThingsMessage response = tryInvokeFunction(request, thingsFunction);
            if (response != null) {
                thingsEventObserver.notifyObservers(new ThingsAsyncResponseEvent(this, response));
            }
        });
    }

    @SneakyThrows
    private JsonThingsMessage tryInvokeFunction(JsonThingsMessage request, ThingsFunction thingsFunction) {
        Object result = thingsFunction.getMethod().invoke(thingsFunction.getBean(), args(request, thingsFunction));
        if (result != null) {
            if (result instanceof JsonThingsMessage) {
                return (JsonThingsMessage) result;
            } else {
                return request.success(result);
            }
        }
        return null;
    }

    private Object[] args(JsonThingsMessage jsonThingsMessage, ThingsFunction thingsFunction) {
        ThingsParameter[] thingsParameters = thingsFunction.getThingsParameters();
        Object[] args = new Object[thingsParameters.length];
        for (int i = 0; i < thingsParameters.length; i++) {
            ThingsParameter thingsParameter = thingsParameters[i];
            War war = new War(jsonThingsMessage, thingsParameter, thingsFunction, i);
            if (thingsParameter.getAnnotation() != null) {
                Function<War, Object> function = functionMap.get(thingsParameter.getAnnotation().annotationType().getName());
                args[i] = function.apply(war);
            } else {
                log.error("Things method args {} convert failed", thingsParameter.getName());
            }
        }
        return args;
    }

    private Object argForThingsPayload(War war) {
        return war.getJsonThingsMessage().getPayload().toJavaObject(war.getThingsParameter().getType());
    }

    private Object argForThingsMetadata(War war) {
        return war.getJsonThingsMessage().getMetadata().toJavaObject(war.getThingsParameter().getType());
    }

    private Object argForThingsInject(War war) {
        ThingsParameter thingsParameter = war.getThingsParameter();
        JsonThingsMessage jsonThingsMessage = war.getJsonThingsMessage();
        ThingsFunction thingsFunction = war.getThingsFunction();
        ThingsProperty annotation = thingsParameter.getType().getAnnotation(ThingsProperty.class);
        if (annotation != null) {
            String productCode = jsonThingsMessage.getBaseMetadata().getProductCode();
            if (annotation.productCode().equals(productCode)) {
                if (annotation.productPublic()) {
                    return thingsPropertier.getProperties(productCode);
                } else {
                    return thingsPropertier.getProperties(productCode, jsonThingsMessage.getBaseMetadata().getDeviceCode());
                }
            }
            log.error("物模型方法调用需要注入的配置对象与产品标识不一致（{}），方法：{}，参数：{}", productCode, thingsFunction.getMethod().getName(), thingsParameter.getName());
            return null;
        }
        return thingsFunction.getThingsContainer().getBean(thingsParameter.getType());
    }

    private Object argForThingsMessage(War war) {
        ThingsParameter thingsParameter = war.getThingsParameter();
        JsonThingsMessage jsonThingsMessage = war.getJsonThingsMessage();
        ThingsFunction thingsFunction = war.getThingsFunction();
        if (thingsParameter.getType().isAssignableFrom(JsonThingsMessage.class)) {
            return jsonThingsMessage;
        }
        if (thingsParameter.getType().isAssignableFrom(BaseThingsMessage.class) || thingsParameter.getType().isAssignableFrom(AbstractThingsMessage.class)) {
            return typeConvert(jsonThingsMessage, thingsParameter.getType(), thingsFunction.getMethod(), war.getIndex());
        }
        return JSON.to(thingsParameter.getType(), jsonThingsMessage);
    }

    private Object argForThingsParam(War war) {
        ThingsParameter thingsParameter = war.getThingsParameter();
        JsonThingsMessage jsonThingsMessage = war.getJsonThingsMessage();
        ThingsParam.BodyType object = ((ThingsParam) thingsParameter.getAnnotation()).bodyType();
        if (ThingsParam.BodyType.PAYLOAD.equals(object)) {
            return jsonThingsMessage.getPayload().getObject(thingsParameter.getName(), thingsParameter.getType());
        } else if (ThingsParam.BodyType.METADATA.equals(object)) {
            return jsonThingsMessage.getMetadata().getObject(thingsParameter.getName(), thingsParameter.getType());
        }
        return Convert.convert(thingsParameter.getType(), ReflectUtil.getFieldValue(jsonThingsMessage, thingsParameter.getName()));
    }

    @Data
    @AllArgsConstructor
    private static class War {
        private JsonThingsMessage jsonThingsMessage;
        private ThingsParameter thingsParameter;
        private ThingsFunction thingsFunction;
        private int index;
    }
}
