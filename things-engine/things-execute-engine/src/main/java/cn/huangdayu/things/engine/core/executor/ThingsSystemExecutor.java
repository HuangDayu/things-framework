package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.container.ThingsDescriber;
import cn.huangdayu.things.api.infrastructure.ThingsConfigurator;
import cn.huangdayu.things.common.annotation.ThingsParams;
import cn.huangdayu.things.common.annotation.ThingsAction;
import cn.huangdayu.things.common.annotation.ThingsSystem;
import cn.huangdayu.things.common.dsl.ThingsDslInfo;
import cn.huangdayu.things.common.properties.ThingsEngineProperties;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.common.constants.ThingsConstants.SystemMethod.*;
import static cn.huangdayu.things.common.utils.ThingsUtils.jsonToObject;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsSystem(name = "系统执行器")
public class ThingsSystemExecutor {

    private final ThingsDescriber thingsDescriber;
    private final ThingsConfigurator thingsConfigurator;

    @ThingsAction(identifier = SYSTEM_METHOD_DSL)
    public ThingsDslInfo getDSL() {
        return thingsDescriber.getDSL();
    }

    @ThingsAction(identifier = SYSTEM_METHOD_CONFIG_SET)
    public void setConfig(@ThingsParams JSONObject params) {
        thingsConfigurator.updateProperties(jsonToObject(params, ThingsEngineProperties.class));
    }


    @ThingsAction(identifier = SYSTEM_METHOD_CONFIG_GET)
    public ThingsEngineProperties getConfig() {
        return thingsConfigurator.getProperties();
    }

}
