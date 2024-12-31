package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.common.annotation.*;
import cn.huangdayu.things.common.message.AbstractThingsMessage;
import cn.huangdayu.things.common.message.BaseThingsMessage;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.engine.core.ThingsProperties;
import cn.huangdayu.things.engine.wrapper.ThingsFunction;
import cn.huangdayu.things.engine.wrapper.ThingsParameter;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Function;

import static cn.huangdayu.things.common.utils.ThingsUtils.typeConvert;
import static cn.huangdayu.things.engine.core.executor.ThingsBaseExecutor.getThingsBean;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
@RequiredArgsConstructor
public class ThingsArgsConverter {
    private final ThingsProperties thingsProperties;


    private final Map<String, Function<ThingsArgsConverter.War, Object>> functionMap = Map.of(
            ThingsParam.class.getName(), this::argForThingsParam,
            ThingsMessage.class.getName(), this::argForThingsMessage,
            ThingsPayload.class.getName(), this::argForThingsPayload,
            ThingsMetadata.class.getName(), this::argForThingsMetadata,
            ThingsInject.class.getName(), this::argForThingsInject
    );


    public Object[] args(JsonThingsMessage jtm, ThingsFunction thingsFunction) {
        ThingsParameter[] thingsParameters = thingsFunction.getThingsParameters();
        Object[] args = new Object[thingsParameters.length];
        for (int i = 0; i < thingsParameters.length; i++) {
            ThingsParameter thingsParameter = thingsParameters[i];
            War war = new War(jtm, thingsParameter, thingsFunction, i);
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
        return war.getJtm().getPayload().toJavaObject(war.getThingsParameter().getType());
    }

    private Object argForThingsMetadata(War war) {
        return war.getJtm().getMetadata().toJavaObject(war.getThingsParameter().getType());
    }

    private Object argForThingsInject(War war) {
        ThingsParameter thingsParameter = war.getThingsParameter();
        JsonThingsMessage jtm = war.getJtm();
        ThingsFunction thingsFunction = war.getThingsFunction();
        ThingsPropertyEntity annotation = thingsParameter.getType().getAnnotation(ThingsPropertyEntity.class);
        if (annotation != null) {
            String productCode = jtm.getBaseMetadata().getProductCode();
            if (annotation.productCode().equals(productCode)) {
                if (annotation.productPublic()) {
                    return thingsProperties.getPropertyEntity(productCode);
                } else {
                    return thingsProperties.getPropertyEntity(productCode, jtm.getBaseMetadata().getDeviceCode());
                }
            }
            log.error("物模型方法调用需要注入的配置对象与产品标识不一致（{}），方法：{}，参数：{}", productCode, thingsFunction.getMethod().getName(), thingsParameter.getName());
            return null;
        }
        return getThingsBean(thingsParameter.getType());
    }

    private Object argForThingsMessage(War war) {
        ThingsParameter thingsParameter = war.getThingsParameter();
        JsonThingsMessage jtm = war.getJtm();
        ThingsFunction thingsFunction = war.getThingsFunction();
        if (thingsParameter.getType().isAssignableFrom(JsonThingsMessage.class)) {
            return jtm;
        }
        if (thingsParameter.getType().isAssignableFrom(BaseThingsMessage.class) || thingsParameter.getType().isAssignableFrom(AbstractThingsMessage.class)) {
            return typeConvert(jtm, thingsParameter.getType(), thingsFunction.getMethod(), war.getIndex());
        }
        return JSON.to(thingsParameter.getType(), jtm);
    }

    private Object argForThingsParam(War war) {
        ThingsParameter thingsParameter = war.getThingsParameter();
        JsonThingsMessage jtm = war.getJtm();
        ThingsParam.BodyType object = ((ThingsParam) thingsParameter.getAnnotation()).bodyType();
        if (ThingsParam.BodyType.PAYLOAD.equals(object)) {
            return jtm.getPayload().getObject(thingsParameter.getName(), thingsParameter.getType());
        } else if (ThingsParam.BodyType.METADATA.equals(object)) {
            return jtm.getMetadata().getObject(thingsParameter.getName(), thingsParameter.getType());
        }
        return Convert.convert(thingsParameter.getType(), ReflectUtil.getFieldValue(jtm, thingsParameter.getName()));
    }

    @Data
    @AllArgsConstructor
    private static class War {
        private JsonThingsMessage jtm;
        private ThingsParameter thingsParameter;
        private ThingsFunction thingsFunction;
        private int index;
    }
}
