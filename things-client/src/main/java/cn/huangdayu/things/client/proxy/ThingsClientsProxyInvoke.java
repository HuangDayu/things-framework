package cn.huangdayu.things.client.proxy;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.common.annotation.*;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.message.AbstractThingsMessage;
import cn.huangdayu.things.common.message.BaseThingsMessage;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.utils.ThingsUtils;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
@Slf4j
public class ThingsClientsProxyInvoke {

    private final ThingsEndpointFactory thingsEndpointFactory;

    public Object invokeService(ThingsClient thingsClient, ThingsService thingsService, Method method, Object[] args) {
        return invoke(method, buildThingsMessage(thingsClient, thingsService, method, args));
    }


    private Object invoke(Method method, JsonThingsMessage request) {
        JsonThingsMessage response = thingsEndpointFactory.create(request).send(request);
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
        return response.getPayload().toJavaObject(returnType);
    }


    public JsonThingsMessage buildThingsMessage(ThingsClient thingsClient, ThingsService thingsService, Method method, Object[] args) {
        String productCode = StrUtil.isNotBlank(thingsClient.productCode()) ? thingsClient.productCode() : thingsService.productCode();
        String identifier = StrUtil.isNotBlank(thingsService.identifier()) ? thingsService.identifier() : method.getName();
        JsonThingsMessage jsonThingsMessage = buildThingsMessage(method, args);
        jsonThingsMessage.setBaseMetadata(baseThingsMetadata -> {
            baseThingsMetadata.setProductCode(productCode);
            if (StrUtil.isNotBlank(thingsClient.uri())) {
                baseThingsMetadata.setTarget(thingsClient.uri());
            }
        });
        jsonThingsMessage.setMethod(ThingsConstants.Methods.SERVICE_START_WITH.concat(identifier));
        return jsonThingsMessage;
    }

    public JsonThingsMessage buildThingsMessage(Method method, Object[] args) {
        JsonThingsMessage jsonThingsMessage = new JsonThingsMessage();
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            Object argValue = args[i];
            if (argValue != null) {
                try {
                    if (parameter.getAnnotation(ThingsMessage.class) != null) {
                        jsonThingsMessage = JSON.parseObject(JSON.toJSONString(argValue), JsonThingsMessage.class);
                    } else if (parameter.getAnnotation(ThingsMetadata.class) != null) {
                        jsonThingsMessage.getMetadata().putAll((JSONObject) JSON.toJSON(argValue));
                    } else if (parameter.getAnnotation(ThingsPayload.class) != null) {
                        jsonThingsMessage.getPayload().putAll((JSONObject) JSON.toJSON(argValue));
                    } else if (parameter.getAnnotation(ThingsParam.class) != null) {
                        ThingsParam thingsParam = parameter.getAnnotation(ThingsParam.class);
                        String identifier = StrUtil.isNotBlank(thingsParam.identifier()) ? thingsParam.identifier() : parameter.getName();
                        if (thingsParam.bodyType().equals(ThingsParam.BodyType.PAYLOAD)) {
                            jsonThingsMessage.getPayload().put(identifier, argValue);
                        } else {
                            jsonThingsMessage.getMetadata().put(identifier, argValue);
                        }
                    } else {
                        jsonThingsMessage.getPayload().put(parameter.getName(), argValue);
                    }
                } catch (Exception e) {
                    log.error("Things client invoke , method {} arg {} convert exception : ", method.getName(), parameter.getName(), e);
                }
            }
        }
        return jsonThingsMessage;
    }

}
