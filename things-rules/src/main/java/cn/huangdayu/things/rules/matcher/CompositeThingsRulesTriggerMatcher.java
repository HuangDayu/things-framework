package cn.huangdayu.things.rules.matcher;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.rules.ThingsRulesHandlerFactory;
import cn.huangdayu.things.rules.generator.ConditionIdGenerator;
import cn.huangdayu.things.api.rules.ThingsRulesTriggerMatcher;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 复合触发器匹配器
 * 负责匹配复合类型的触发器
 * 处理包含多个子条件的复杂触发器与消息之间的匹配逻辑
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class CompositeThingsRulesTriggerMatcher implements ThingsRulesTriggerMatcher {

    /**
     * 匹配复合触发器条件
     * 检查传入的消息是否满足复合触发器定义的所有子条件
     *
     * @param condition 触发条件对象
     * @param message   触发规则执行的消息
     * @return 如果匹配成功返回true，否则返回false
     */
    @Override
    public boolean match(ThingsRules.TriggerCondition condition, ThingsRequestMessage message) {
        // 检查复合触发器是否有效
        if (isInvalidCompositeTrigger(condition)) {
            return false;
        }

        // 执行复合触发器匹配
        return doMatchCompositeTrigger(condition, message);
    }

    /**
     * 获取触发器类型
     *
     * @return 触发器类型字符串
     */
    @Override
    public String getType() {
        return "composite";
    }

    /**
     * 检查复合触发器是否无效
     * 验证复合触发器是否包含有效的子条件
     *
     * @param condition 触发条件对象
     * @return 如果触发器无效返回true，否则返回false
     */
    private boolean isInvalidCompositeTrigger(ThingsRules.TriggerCondition condition) {
        return condition.getConditions() == null || condition.getConditions().isEmpty();
    }

    /**
     * 执行复合触发器匹配
     * 根据逻辑操作符匹配所有子条件
     *
     * @param condition 触发条件对象
     * @param message   触发规则执行的消息
     * @return 如果匹配成功返回true，否则返回false
     */
    private boolean doMatchCompositeTrigger(ThingsRules.TriggerCondition condition, ThingsRequestMessage message) {
        String logicOperator = condition.getLogicOperator();
        List<ThingsRules.TriggerCondition> conditions = condition.getConditions();

        // 过滤掉无效的条件
        conditions.removeIf(this::isInvalidCondition);

        // 如果没有有效条件，返回false
        if (conditions.isEmpty()) {
            return false;
        }

        // 根据逻辑操作符进行匹配
        if ("AND".equalsIgnoreCase(logicOperator)) {
            return matchAllConditions(conditions, message);
        } else if ("OR".equalsIgnoreCase(logicOperator)) {
            return matchAnyCondition(conditions, message);
        } else {
            // 默认使用AND逻辑
            return matchAllConditions(conditions, message);
        }
    }

    /**
     * 检查条件是否无效
     *
     * @param condition 触发条件对象
     * @return 如果条件无效返回true，否则返回false
     */
    private boolean isInvalidCondition(ThingsRules.TriggerCondition condition) {
        return condition == null;
    }

    /**
     * 匹配所有条件（AND逻辑）
     * 所有子条件都必须匹配才算匹配成功
     *
     * @param conditions 条件列表
     * @param message    触发规则执行的消息
     * @return 如果所有条件都匹配返回true，否则返回false
     */
    private boolean matchAllConditions(List<ThingsRules.TriggerCondition> conditions, ThingsRequestMessage message) {
        for (ThingsRules.TriggerCondition condition : conditions) {
            String triggerType = ConditionIdGenerator.determineTriggerType(condition);
            ThingsRulesTriggerMatcher matcher = ThingsRulesHandlerFactory.getTriggerMatcher(triggerType);
            if (matcher == null || !matcher.match(condition, message)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 匹配任一条件（OR逻辑）
     * 任一子条件匹配就算匹配成功
     *
     * @param conditions 条件列表
     * @param message    触发规则执行的消息
     * @return 如果任一条件匹配返回true，否则返回false
     */
    private boolean matchAnyCondition(List<ThingsRules.TriggerCondition> conditions, ThingsRequestMessage message) {
        for (ThingsRules.TriggerCondition condition : conditions) {
            String triggerType = ConditionIdGenerator.determineTriggerType(condition);
            ThingsRulesTriggerMatcher matcher = ThingsRulesHandlerFactory.getTriggerMatcher(triggerType);
            if (matcher != null && matcher.match(condition, message)) {
                return true;
            }
        }
        return false;
    }


}