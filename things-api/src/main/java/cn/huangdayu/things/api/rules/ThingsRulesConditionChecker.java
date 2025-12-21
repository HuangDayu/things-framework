package cn.huangdayu.things.api.rules;

import cn.huangdayu.things.common.dsl.rules.ThingsRules;

/**
 * 执行条件检查器接口
 *
 * @author huangdayu
 */
public interface ThingsRulesConditionChecker extends ThingsRulesHandler{

    /**
     * 检查执行条件
     *
     * @param executionCondition 执行条件
     * @return 是否满足条件
     */
    boolean check(ThingsRules.ExecutionCondition executionCondition);


}