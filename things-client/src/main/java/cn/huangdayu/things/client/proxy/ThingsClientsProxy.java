package cn.huangdayu.things.client.proxy;

import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.common.annotation.*;
import cn.huangdayu.things.common.message.AbstractThingsMessage;
import cn.huangdayu.things.common.message.BaseThingsMessage;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.utils.ThingsUtils;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.THINGS_IDENTIFIER;
import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.THINGS_SERVICE_REQUEST;
import static cn.huangdayu.things.common.utils.ThingsUtils.getReturnType;
import static cn.huangdayu.things.common.utils.ThingsUtils.jsonToObject;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
@Slf4j
public class ThingsClientsProxy {

    private final ThingsPublisher thingsPublisher;

    public Object invokeService(ThingsClient thingsClient, ThingsService thingsService, Method method, Object[] args) {
        JsonThingsMessage jtm = buildThingsMessage(thingsClient, thingsService, method, args);
        if (method.getReturnType().isAssignableFrom(Publisher.class)) {
            return reactorInvoke(method, jtm);
        }
        if (method.getReturnType().isAssignableFrom(void.class) || method.getReturnType().isAssignableFrom(Void.class)) {
            thingsPublisher.asyncSendMessage(jtm, null);
            return null;
        }
        return syncInvoke(method, jtm);
    }

    @SneakyThrows
    private Object reactorInvoke(Method method, JsonThingsMessage request) {
        Mono<JsonThingsMessage> jtm = thingsPublisher.reactorSendMessage(request);
        Type type = getReturnType(method)[0];
        if (method.getReturnType().isAssignableFrom(Mono.class)) {
            if (type.equals(JsonThingsMessage.class)) {
                return jtm;
            }
            return Mono.just(jsonToObject(jtm.block().getPayload(), type));
        }

        if (method.getReturnType().isAssignableFrom(Flux.class)) {
            if (type.equals(JsonThingsMessage.class)) {
                return jtm.flux();
            }
            return Flux.just(jsonToObject(jtm.block().getPayload(), type));
        }
        return jsonToObject(jtm.block().toJson(), type);
    }

    private Object syncInvoke(Method method, JsonThingsMessage request) {
        JsonThingsMessage response = thingsPublisher.syncSendMessage(request);
        if (response == null) {
            return null;
        }
        Class<?> returnType = method.getReturnType();
        if (returnType.isAssignableFrom(Void.class) || returnType.isAssignableFrom(void.class)) {
            return null;
        } else if (returnType.isAssignableFrom(JsonThingsMessage.class)) {
            return response;
        } else if (returnType.isAssignableFrom(BaseThingsMessage.class) || returnType.isAssignableFrom(AbstractThingsMessage.class)) {
            return ThingsUtils.returnTypeConvert(response, returnType, method);
        } else if (returnType.isAssignableFrom(String.class)) {
            return response.getPayload().toJSONString();
        }
        return jsonToObject(response.getPayload(), returnType);
    }


    public JsonThingsMessage buildThingsMessage(ThingsClient thingsClient, ThingsService thingsService, Method method, Object[] args) {
        String productCode = StrUtil.isNotBlank(thingsClient.productCode()) ? thingsClient.productCode() : thingsService.productCode();
        String identifier = StrUtil.isNotBlank(thingsService.identifier()) ? thingsService.identifier() : method.getName();
        JsonThingsMessage jtm = buildThingsMessage(method, args);
        jtm.setBaseMetadata(baseThingsMetadata -> {
            baseThingsMetadata.setProductCode(productCode);
            if (StrUtil.isNotBlank(thingsClient.targetCode())) {
                baseThingsMetadata.setTargetCode(thingsClient.targetCode());
            }
        });
        jtm.setMethod(THINGS_SERVICE_REQUEST.replace(THINGS_IDENTIFIER, identifier));
        return jtm;
    }

    public JsonThingsMessage buildThingsMessage(Method method, Object[] args) {
        JsonThingsMessage jtm = new JsonThingsMessage();
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            Object argValue = args[i];
            if (argValue != null) {
                try {
                    if (parameter.getAnnotation(ThingsMessage.class) != null) {
                        jtm = JSON.parseObject(JSON.toJSONString(argValue), JsonThingsMessage.class);
                    } else if (parameter.getAnnotation(ThingsMetadata.class) != null) {
                        jtm.getMetadata().putAll((JSONObject) JSON.toJSON(argValue));
                    } else if (parameter.getAnnotation(ThingsPayload.class) != null) {
                        jtm.getPayload().putAll((JSONObject) JSON.toJSON(argValue));
                    } else if (parameter.getAnnotation(ThingsParam.class) != null) {
                        ThingsParam thingsParam = parameter.getAnnotation(ThingsParam.class);
                        String identifier = StrUtil.isNotBlank(thingsParam.identifier()) ? thingsParam.identifier() : parameter.getName();
                        if (thingsParam.bodyType().equals(ThingsParam.BodyType.PAYLOAD)) {
                            jtm.getPayload().put(identifier, argValue);
                        } else {
                            jtm.getMetadata().put(identifier, argValue);
                        }
                    } else {
                        jtm.getPayload().put(parameter.getName(), argValue);
                    }
                } catch (Exception e) {
                    log.error("Things client invoke , method {} arg {} convert exception : ", method.getName(), parameter.getName(), e);
                }
            }
        }
        return jtm;
    }

}
