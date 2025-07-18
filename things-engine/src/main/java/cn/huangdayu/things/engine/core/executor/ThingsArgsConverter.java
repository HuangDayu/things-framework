package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.common.annotation.*;
import cn.huangdayu.things.common.message.AbstractThingsMessage;
import cn.huangdayu.things.common.message.ThingsMessageMethod;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.engine.core.ThingsProperties;
import cn.huangdayu.things.engine.wrapper.ThingsFunction;
import cn.huangdayu.things.engine.wrapper.ThingsParameter;
import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Function;

import static cn.huangdayu.things.common.utils.ThingsUtils.jsonToObject;
import static cn.huangdayu.things.common.utils.ThingsUtils.typeConvert;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
@RequiredArgsConstructor
public class ThingsArgsConverter {
    private final ThingsProperties thingsProperties;
    private final ThingsContainerManager thingsContainerManager;


    private final Map<String, Function<ThingsArgsConverter.War, Object>> functionMap = Map.of(
            ThingsParam.class.getName(), this::argForThingsParam,
            ThingsMessage.class.getName(), this::argForThingsMessage,
            ThingsParams.class.getName(), this::argForThingsParams,
            ThingsMethod.class.getName(), this::argForThingsMethod,
            ThingsInject.class.getName(), this::argForThingsInject
    );


    public Object[] args(ThingsRequestMessage trm, ThingsFunction thingsFunction) {
        ThingsParameter[] thingsParameters = thingsFunction.getThingsParameters();
        Object[] args = new Object[thingsParameters.length];
        for (int i = 0; i < thingsParameters.length; i++) {
            ThingsParameter thingsParameter = thingsParameters[i];
            War war = new War(trm, thingsParameter, thingsFunction, i);
            if (thingsParameter.getAnnotation() != null) {
                Function<War, Object> function = functionMap.get(thingsParameter.getAnnotation().annotationType().getName());
                args[i] = function.apply(war);
            } else {
                log.error("Things method args {} convert failed", thingsParameter.getName());
            }
        }
        return args;
    }

    private Object argForThingsParams(War war) {
        return jsonToObject(war.getTrm().getParams(), war.getThingsParameter().getType());
    }

    private Object argForThingsMethod(War war) {
        String method = war.getTrm().getMethod();
        ThingsMessageMethod messageMethod = new ThingsMessageMethod(method);
        ThingsMethod annotation = (ThingsMethod) war.getThingsParameter().getAnnotation();
        if (annotation.productCode()) {
            return Convert.convert(war.getThingsParameter().getType(), messageMethod.getProductCode());
        } else if (annotation.deviceCode()) {
            return Convert.convert(war.getThingsParameter().getType(), messageMethod.getDeviceCode());
        } else if (annotation.type()) {
            return Convert.convert(war.getThingsParameter().getType(), messageMethod.getType());
        } else if (annotation.identifier()) {
            return Convert.convert(war.getThingsParameter().getType(), messageMethod.getIdentifier());
        } else if (annotation.action()) {
            return Convert.convert(war.getThingsParameter().getType(), messageMethod.getAction());
        }
        return Convert.convert(war.getThingsParameter().getType(), method);
    }

    private Object argForThingsInject(War war) {
        ThingsParameter thingsParameter = war.getThingsParameter();
        ThingsRequestMessage trm = war.getTrm();
        ThingsFunction thingsFunction = war.getThingsFunction();
        ThingsPropertyEntity annotation = thingsParameter.getType().getAnnotation(ThingsPropertyEntity.class);
        if (annotation != null) {
            String productCode = trm.getMessageMethod().getProductCode();
            if (annotation.productCode().equals(productCode)) {
                if (annotation.productPublic()) {
                    return thingsProperties.getPropertyEntity(productCode);
                } else {
                    return thingsProperties.getPropertyEntity(productCode, trm.getMessageMethod().getDeviceCode());
                }
            }
            log.error("物模型方法调用需要注入的配置对象与产品标识不一致（{}），方法：{}，参数：{}", productCode, thingsFunction.getMethod().getName(), thingsParameter.getName());
            return null;
        }
        return thingsContainerManager.getThingsBean(thingsParameter.getType());
    }

    private Object argForThingsMessage(War war) {
        ThingsParameter thingsParameter = war.getThingsParameter();
        ThingsRequestMessage trm = war.getTrm();
        ThingsFunction thingsFunction = war.getThingsFunction();
        if (thingsParameter.getType().isAssignableFrom(ThingsRequestMessage.class)) {
            return trm;
        }
        if (thingsParameter.getType().isAssignableFrom(AbstractThingsMessage.class)) {
            return typeConvert(trm, thingsParameter.getType(), thingsFunction.getMethod(), war.getIndex());
        }
        return JSON.to(thingsParameter.getType(), trm);
    }

    private Object argForThingsParam(War war) {
        ThingsParameter thingsParameter = war.getThingsParameter();
        ThingsRequestMessage trm = war.getTrm();
        return trm.getParams().getObject(thingsParameter.getName(), thingsParameter.getType());
    }

    @Data
    @AllArgsConstructor
    private static class War {
        private ThingsRequestMessage trm;
        private ThingsParameter thingsParameter;
        private ThingsFunction thingsFunction;
        private int index;
    }
}
