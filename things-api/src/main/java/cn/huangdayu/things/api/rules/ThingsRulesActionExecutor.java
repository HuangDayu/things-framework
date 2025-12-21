package cn.huangdayu.things.api.rules;

import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import com.alibaba.fastjson2.JSONObject;

/**
 * 动作执行器接口
 *
 * @author huangdayu
 */
public interface ThingsRulesActionExecutor extends ThingsRulesHandler{

    /**
     * 执行动作
     *
     * @param params 动作参数
     * @return 执行结果
     */
    JSONObject execute(ThingsRules.ActionParams params);

}