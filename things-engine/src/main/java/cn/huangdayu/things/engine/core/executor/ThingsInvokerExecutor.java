package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.BaseThingsMetadata;
import cn.huangdayu.things.common.message.JsonThingsMessage;
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
import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.THINGS_PROPERTIES_POST;
import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.THINGS_PROPERTIES_SET;
import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_WILDCARD;
import static cn.huangdayu.things.common.factory.ThreadPoolFactory.THINGS_EXECUTOR;
import static cn.huangdayu.things.common.utils.ThingsUtils.*;

/**
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
@ThingsBean
public class ThingsInvokerExecutor implements ThingsInvoker {

    private final ThingsProperties thingsProperties;
    private final ThingsArgsConverter thingsArgsConverter;
    private final ThingsContainerManager thingsContainerManager;

    @Override
    public boolean canInvoke(JsonThingsMessage jtm) {
        if (jtm.isResponse()) {
            return false;
        }
        BaseThingsMetadata baseMetadata = jtm.getBaseMetadata();
        if (isEventPost(jtm)) {
            return thingsContainerManager.getThingsEventsListenerTable().containsColumn(baseMetadata.getProductCode());
        }
        if (isPropertiesSetOrGet(jtm)) {
            return thingsContainerManager.getThingsPropertyMap().containsKey(baseMetadata.getProductCode()) ||
                    thingsContainerManager.getDevicePropertyMap().containsColumn(baseMetadata.getDeviceCode());
        }
        if (isServiceRequest(jtm)) {
            return thingsContainerManager.getThingsFunctionTable().containsColumn(baseMetadata.getProductCode());
        }
        return false;
    }

    @SneakyThrows
    @Override
    public JsonThingsMessage syncInvoke(JsonThingsMessage jtm) {
        if (isServiceRequest(jtm)) {
            return invokeService(jtm);
        } else if (isEventPost(jtm)) {
            return invokeEventListener(jtm);
        } else if (isPropertiesSetOrGet(jtm)) {
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
        Set<ThingsFunction> thingsFunctions = thingsContainerManager.getThingsEventsListenerTable().get(identifies, productCode);
        if (CollUtil.isNotEmpty(thingsFunctions)) {
            functions.addAll(thingsFunctions);
        }
        Set<ThingsFunction> thingsFunctions1 = thingsContainerManager.getThingsEventsListenerTable().get(identifies, THINGS_WILDCARD);
        if (CollUtil.isNotEmpty(thingsFunctions1)) {
            functions.addAll(thingsFunctions1);
        }
        Set<ThingsFunction> thingsFunctions2 = thingsContainerManager.getThingsEventsListenerTable().get(THINGS_WILDCARD, productCode);
        if (CollUtil.isNotEmpty(thingsFunctions2)) {
            functions.addAll(thingsFunctions2);
        }
        return functions;
    }


    private JsonThingsMessage invokeUpdateProperty(JsonThingsMessage jtm) {
        BaseThingsMetadata baseThingsMetadata = jtm.getBaseMetadata();
        Object propertyBean = thingsProperties.getPropertyEntity(baseThingsMetadata.getProductCode(), baseThingsMetadata.getDeviceCode());
        if (propertyBean != null) {
            return invokeUpdateProperty(propertyBean, jtm, baseThingsMetadata);
        }
        throw new ThingsException(jtm, BAD_REQUEST, "Things ont found Property entry.");
    }

    private JsonThingsMessage invokeUpdateProperty(Object propertyBean, JsonThingsMessage jtm, BaseThingsMetadata baseThingsMetadata) {
        String method = jtm.getMethod();
        if (method.equals(THINGS_PROPERTIES_SET)) {
            JSONObject payload = jtm.getPayload();
            for (Map.Entry<String, Object> entry : payload.entrySet()) {
                ReflectUtil.setFieldValue(propertyBean, entry.getKey(), entry.getValue());
                asyncInvokeFunctions(jtm, thingsContainerManager.getThingsPropertyListenerTable().get(entry.getKey(), baseThingsMetadata.getProductCode()));
            }
            asyncInvokeFunctions(jtm, thingsContainerManager.getThingsPropertyListenerTable().get(THINGS_WILDCARD, baseThingsMetadata.getProductCode()));
        }
        jtm.setPayload((JSONObject) JSON.toJSON(propertyBean));
        jtm.setMethod(THINGS_PROPERTIES_POST);
        return jtm;
    }

    @SneakyThrows
    private JsonThingsMessage invokeService(JsonThingsMessage jtm) {
        ThingsFunction thingsFunction = thingsContainerManager.getThingsFunctionTable().get(subIdentifies(jtm.getMethod()), jtm.getBaseMetadata().getProductCode());
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
        return null;
    }

    @SneakyThrows
    private JsonThingsMessage syncInvokeFunction(JsonThingsMessage jtm, ThingsFunction thingsFunction) {
        Object result = thingsFunction.getMethod().invoke(thingsFunction.getBean(), thingsArgsConverter.args(jtm, thingsFunction));
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
