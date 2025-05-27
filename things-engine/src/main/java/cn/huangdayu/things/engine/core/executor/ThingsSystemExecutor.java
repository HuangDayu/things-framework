package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.container.ThingsDescriber;
import cn.huangdayu.things.common.annotation.Things;
import cn.huangdayu.things.common.annotation.ThingsPayload;
import cn.huangdayu.things.common.annotation.ThingsService;
import cn.huangdayu.things.common.annotation.ThingsSystem;
import cn.huangdayu.things.common.dsl.DslInfo;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.common.constants.ThingsConstants.SystemMethod.SYSTEM_METHOD_CONFIG;
import static cn.huangdayu.things.common.constants.ThingsConstants.SystemMethod.SYSTEM_METHOD_DSL;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsSystem(name = "系统执行器", productCode = "SYSTEM")
public class ThingsSystemExecutor {

    private final ThingsDescriber thingsDescriber;

    @ThingsService(identifier = SYSTEM_METHOD_DSL)
    public DslInfo getDSL() {
        return thingsDescriber.getDsl();
    }

    @ThingsService(identifier = SYSTEM_METHOD_CONFIG)
    public void config(@ThingsPayload JSONObject payload) {

    }

}
