package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.ThingsMessageMethod;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.message.ThingsResponseMessage;
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
import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.THINGS_POST;
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
    public boolean canInvoke(ThingsRequestMessage trm) {
        if (trm == null) {
            return false;
        }
        ThingsMessageMethod messageMethod = trm.getMessageMethod();
        if (isEventPost(trm)) {
            return thingsContainerManager.getThingsEventsListenerTable().containsColumn(messageMethod.getProductCode());
        }
        if (isPropertiesSetOrGet(trm)) {
            return thingsContainerManager.getThingsPropertyMap().containsKey(messageMethod.getProductCode()) ||
                    thingsContainerManager.getDevicePropertyMap().containsColumn(messageMethod.getDeviceCode());
        }
        if (isServiceRequest(trm)) {
            return thingsContainerManager.getThingsFunctionTable().containsColumn(messageMethod.getProductCode());
        }
        return false;
    }

    @SneakyThrows
    @Override
    public ThingsResponseMessage syncInvoke(ThingsRequestMessage trm) {
        if (isServiceRequest(trm)) {
            return invokeService(trm);
        } else if (isEventPost(trm)) {
            return invokeEventListener(trm);
        } else if (isPropertiesSetOrGet(trm)) {
            return invokeUpdateProperty(trm);
        } else {
            throw new ThingsException(trm, BAD_REQUEST, "Can't handler this message.");
        }
    }


    @Override
    public Mono<ThingsResponseMessage> reactorInvoke(ThingsRequestMessage trm) {
        return Mono.just(syncInvoke(trm));
    }

    private ThingsResponseMessage invokeEventListener(ThingsRequestMessage trm) {
        String method = subIdentifies(trm.getMethod());
        Set<ThingsFunction> functions = findEventListenerFunction(method, trm.getMessageMethod().getProductCode());
        if (CollUtil.isNotEmpty(functions)) {
            return asyncInvokeFunctions(trm, functions);
        }
        throw new ThingsException(trm, BAD_REQUEST, "Things not found this event listener.");
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


    private ThingsResponseMessage invokeUpdateProperty(ThingsRequestMessage trm) {
        ThingsMessageMethod thingsMessageMethod = trm.getMessageMethod();
        Object propertyBean = thingsProperties.getPropertyEntity(thingsMessageMethod.getProductCode(), thingsMessageMethod.getDeviceCode());
        if (propertyBean != null) {
            return invokeUpdateProperty(propertyBean, trm, thingsMessageMethod);
        }
        throw new ThingsException(trm, BAD_REQUEST, "Things ont found Property entry.");
    }

    private ThingsResponseMessage invokeUpdateProperty(Object propertyBean, ThingsRequestMessage thingsRequestMessage, ThingsMessageMethod thingsMessageMethod) {
        if (isPropertiesSetOrGet(thingsRequestMessage)) {
            JSONObject params = thingsRequestMessage.getParams();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                ReflectUtil.setFieldValue(propertyBean, entry.getKey(), entry.getValue());
                asyncInvokeFunctions(thingsRequestMessage, thingsContainerManager.getThingsPropertyListenerTable().get(entry.getKey(), thingsMessageMethod.getProductCode()));
            }
            asyncInvokeFunctions(thingsRequestMessage, thingsContainerManager.getThingsPropertyListenerTable().get(THINGS_WILDCARD, thingsMessageMethod.getProductCode()));
        }
        ThingsResponseMessage thingsResponseMessage = thingsRequestMessage.success();
        thingsResponseMessage.setResult((JSONObject) JSON.toJSON(propertyBean));
        ThingsMessageMethod messageMethod = thingsResponseMessage.getMessageMethod();
        messageMethod.setAction(THINGS_POST);
        thingsResponseMessage.setMethod(messageMethod);
        return thingsResponseMessage;
    }

    @SneakyThrows
    private ThingsResponseMessage invokeService(ThingsRequestMessage trm) {
        ThingsFunction thingsFunction = thingsContainerManager.getThingsFunctionTable().get(subIdentifies(trm.getMethod()), trm.getMessageMethod().getProductCode());
        if (thingsFunction == null) {
            throw new ThingsException(trm, BAD_REQUEST, "Things not found this service.");
        }
        return syncInvokeFunction(trm, thingsFunction);
    }

    private ThingsResponseMessage asyncInvokeFunctions(ThingsRequestMessage trm, Set<ThingsFunction> thingsFunctions) {
        if (CollUtil.isNotEmpty(thingsFunctions)) {
            for (ThingsFunction function : thingsFunctions) {
                THINGS_EXECUTOR.execute(() -> syncInvokeFunction(trm, function));
            }
        }
        return null;
    }

    @SneakyThrows
    private ThingsResponseMessage syncInvokeFunction(ThingsRequestMessage trm, ThingsFunction thingsFunction) {
        Object result = thingsFunction.getMethod().invoke(thingsFunction.getBean(), thingsArgsConverter.args(trm, thingsFunction));
        if (result != null) {
            if (result instanceof ThingsResponseMessage) {
                return (ThingsResponseMessage) result;
            } else if (result instanceof Future future) {
                return getFuture(future, trm);
            } else {
                return trm.success(result);
            }
        }
        return trm.success();
    }

    @SneakyThrows
    private ThingsResponseMessage getFuture(Future future, ThingsRequestMessage request) {
        Object result = future.get(request.getTimeout(), TimeUnit.MILLISECONDS);
        if (result instanceof ThingsResponseMessage) {
            return (ThingsResponseMessage) result;
        }
        return request.success(result);
    }

}
