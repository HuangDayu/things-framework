package cn.huangdayu.things.api.rules;

import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;

/**
 * 触发器匹配器接口
 *
 * @author huangdayu
 */
public interface ThingsRulesTriggerMatcher extends ThingsRulesHandler {

    /**
     * 匹配触发器
     *
     * @param condition 触发条件
     * @param message   物模型消息
     * @return 是否匹配
     */
    boolean match(ThingsRules.TriggerCondition condition, ThingsRequestMessage message);


}