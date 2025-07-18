package cn.huangdayu.things.client.proxy;

import cn.huangdayu.things.api.message.ThingsPublisher;
import cn.huangdayu.things.common.annotation.*;
import cn.huangdayu.things.common.message.AbstractThingsMessage;
import cn.huangdayu.things.common.message.ThingsMessageMethod;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.message.ThingsResponseMessage;
import cn.huangdayu.things.common.utils.ThingsUtils;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.THINGS_REQUEST;
import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.THINGS_SERVICE;
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
        ThingsRequestMessage trm = buildThingsMessage(thingsClient, thingsService, method, args);
        if (method.getReturnType().isAssignableFrom(Publisher.class)) {
            return reactorInvoke(method, trm);
        }
        if (method.getReturnType().isAssignableFrom(void.class) || method.getReturnType().isAssignableFrom(Void.class)) {
            thingsPublisher.asyncSendMessage(trm, null);
            return null;
        }
        return syncInvoke(method, trm);
    }

    @SneakyThrows
    private Object reactorInvoke(Method method, ThingsRequestMessage request) {
        Mono<ThingsResponseMessage> message = thingsPublisher.reactorSendMessage(request);
        Type type = getReturnType(method)[0];
        if (method.getReturnType().isAssignableFrom(Mono.class)) {
            if (type.equals(ThingsRequestMessage.class)) {
                return message;
            }
            return Mono.just(jsonToObject(message.block().getResult(), type));
        }

        if (method.getReturnType().isAssignableFrom(Flux.class)) {
            if (type.equals(ThingsRequestMessage.class)) {
                return message.flux();
            }
            return Flux.just(jsonToObject(message.block().getResult(), type));
        }
        return jsonToObject(message.block().getResult(), type);
    }

    private Object syncInvoke(Method method, ThingsRequestMessage request) {
        ThingsResponseMessage response = thingsPublisher.syncSendMessage(request);
        if (response == null) {
            return null;
        }
        Class<?> returnType = method.getReturnType();
        if (returnType.isAssignableFrom(Void.class) || returnType.isAssignableFrom(void.class)) {
            return null;
        } else if (returnType.isAssignableFrom(ThingsResponseMessage.class)) {
            return response;
        } else if (returnType.isAssignableFrom(AbstractThingsMessage.class)) {
            return ThingsUtils.returnTypeConvert(response, returnType, method);
        }
        return jsonToObject(response.getResult(), returnType);
    }


    public ThingsRequestMessage buildThingsMessage(ThingsClient thingsClient, ThingsService thingsService, Method method, Object[] args) {
        String productCode = StrUtil.isNotBlank(thingsClient.productCode()) ? thingsClient.productCode() : thingsService.productCode();
        String identifier = StrUtil.isNotBlank(thingsService.identifier()) ? thingsService.identifier() : method.getName();
        return buildThingsMessage(productCode, identifier, method, args);
    }

    public ThingsRequestMessage buildThingsMessage(String productCode, String identifier, Method method, Object[] args) {
        ThingsProxyMessage tpm = new ThingsProxyMessage(new ThingsRequestMessage(), new ThingsMessageMethod(productCode, null, THINGS_SERVICE, identifier, THINGS_REQUEST));
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            Object argValue = args[i];
            setThingsMessage(tpm, parameter, argValue);
        }
        ThingsRequestMessage trm = tpm.getTrm();
        trm.setMethod(tpm.getTmm());
        return trm;
    }

    private void setThingsMessage(ThingsProxyMessage tpm, Parameter parameter, Object argValue) {
        ThingsRequestMessage trm = tpm.getTrm();
        if (parameter.getAnnotation(ThingsMessage.class) != null) {
            trm = JSON.parseObject(JSON.toJSONString(argValue), ThingsRequestMessage.class);
        } else if (parameter.getAnnotation(ThingsMethod.class) != null) {
            setMessageMethod(tpm, parameter, argValue);
        } else if (parameter.getAnnotation(ThingsParams.class) != null) {
            trm.getParams().putAll((JSONObject) JSON.toJSON(argValue));
        } else if (parameter.getAnnotation(ThingsParam.class) != null) {
            ThingsParam thingsParam = parameter.getAnnotation(ThingsParam.class);
            trm.getParams().put(StrUtil.isNotBlank(thingsParam.identifier()) ? thingsParam.identifier() : parameter.getName(), argValue);
        } else {
            trm.getParams().put(parameter.getName(), argValue);
        }
        tpm.setTrm(trm);
    }

    private void setMessageMethod(ThingsProxyMessage tpm, Parameter parameter, Object argValue) {
        ThingsMessageMethod tmm = tpm.getTmm();
        if (argValue instanceof ThingsMessageMethod) {
            tmm = (ThingsMessageMethod) argValue;
        } else {
            ThingsMethod annotation = parameter.getAnnotation(ThingsMethod.class);
            if (annotation.productCode()) {
                tmm.setProductCode(Convert.toStr(argValue));
            } else if (annotation.deviceCode()) {
                tmm.setDeviceCode(Convert.toStr(argValue));
            } else if (annotation.type()) {
                tmm.setType(Convert.toStr(argValue));
            } else if (annotation.identifier()) {
                tmm.setIdentifier(Convert.toStr(argValue));
            } else if (annotation.action()) {
                tmm.setAction(Convert.toStr(argValue));
            } else {
                ReflectUtil.setFieldValue(tmm, parameter.getName(), argValue);
            }
        }
        tpm.setTmm(tmm);
    }


    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    private static class ThingsProxyMessage {
        private ThingsRequestMessage trm;
        private ThingsMessageMethod tmm;
    }

}
