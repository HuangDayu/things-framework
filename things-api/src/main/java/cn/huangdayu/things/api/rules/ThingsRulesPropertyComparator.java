package cn.huangdayu.things.api.rules;

import cn.huangdayu.things.common.dsl.rules.ThingsRules;

/**
 * 属性比较器接口
 *
 * @author huangdayu
 */
public interface ThingsRulesPropertyComparator extends ThingsRulesHandler{

    /**
     * 比较属性值
     *
     * @param condition     触发条件
     * @param propertyValue 属性值
     * @return 比较结果
     */
    boolean compare(ThingsRules.TriggerCondition condition, Object propertyValue);

}