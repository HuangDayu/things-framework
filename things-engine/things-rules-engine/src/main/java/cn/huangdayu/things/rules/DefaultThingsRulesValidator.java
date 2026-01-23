package cn.huangdayu.things.rules;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.message.ThingsResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 规则验证器
 * 负责规则执行前的各种验证逻辑
 * 遵循单一职责原则，只负责验证相关的功能
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
@RequiredArgsConstructor
public class DefaultThingsRulesValidator {

    /**
     * 验证规则是否可以执行
     * 注意：此方法假设规则不为null，由调用方保证
     */
    public ThingsResponseMessage validateRuleExecution(ThingsRules thingsRules, ThingsRequestMessage message) {
        if (!isRuleEnabled(thingsRules)) {
            return createDisabledRuleError(thingsRules, message);
        }
        if (!validateExecutionCondition(thingsRules)) {
            return createConditionNotMetError(thingsRules, message);
        }
        return null; // 验证通过
    }

    /**
     * 检查规则是否启用
     */
    private boolean isRuleEnabled(ThingsRules thingsRules) {
        return "enabled".equals(thingsRules.getStatus());
    }

    /**
     * 验证执行条件
     */
    private boolean validateExecutionCondition(ThingsRules thingsRules) {
        if (thingsRules.getExecutionCondition() == null) {
            return true;
        }
        return ThingsRulesHandlerFactory.getAllCheckers()
                .values()
                .stream()
                .allMatch(checker -> checker.check(thingsRules.getExecutionCondition()));
    }

    /**
     * 创建规则禁用错误响应
     */
    private ThingsResponseMessage createDisabledRuleError(ThingsRules thingsRules, ThingsRequestMessage message) {
        log.warn("Rule is disabled: {}", thingsRules.getId());
        return message.clientError("Rule is disabled");
    }

    /**
     * 创建条件不满足错误响应
     */
    private ThingsResponseMessage createConditionNotMetError(ThingsRules thingsRules, ThingsRequestMessage message) {
        log.warn("Execution condition not met for rule: {}", thingsRules.getId());
        return message.clientError("Execution condition not met");
    }
}