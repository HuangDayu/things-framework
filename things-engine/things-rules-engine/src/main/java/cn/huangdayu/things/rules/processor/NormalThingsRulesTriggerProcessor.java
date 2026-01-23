package cn.huangdayu.things.rules.processor;

import cn.huangdayu.things.api.rules.ThingsRulesTriggerMatcher;
import cn.huangdayu.things.api.rules.ThingsRulesTriggerProcessor;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.rules.ThingsRulesHandlerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 普通触发器处理器实现
 * 处理非跨时间的触发器逻辑
 * 遵循单一职责原则，只负责普通触发器的处理
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
@RequiredArgsConstructor
public class NormalThingsRulesTriggerProcessor implements ThingsRulesTriggerProcessor {

    @Override
    public String getType() {
        return "trigger_processor";
    }

    @Override
    public boolean canProcess(ThingsRules thingsRules) {
        return thingsRules.getTriggers() == null || thingsRules.getTriggers().size() < 2;
    }

    @Override
    public boolean matchTriggers(ThingsRules thingsRules, ThingsRequestMessage message) {
        if (thingsRules.getTriggers() == null || thingsRules.getTriggers().isEmpty()) {
            log.warn("No triggers defined for rule: {}", thingsRules.getId());
            return false;
        }
        return processNormalTriggers(thingsRules, message);
    }

    /**
     * 处理普通触发器（非跨时间）
     */
    private boolean processNormalTriggers(ThingsRules thingsRules, ThingsRequestMessage message) {
        for (ThingsRules.Trigger trigger : thingsRules.getTriggers()) {
            if (!processAndLogTrigger(thingsRules, message, trigger)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 处理单个触发器并记录日志
     */
    private boolean processAndLogTrigger(ThingsRules thingsRules, ThingsRequestMessage message, ThingsRules.Trigger trigger) {
        boolean result = processTrigger(thingsRules, message, trigger);
        log.info("Trigger process [{}:{}] result : {}", thingsRules.getId(), trigger.getType(), result);
        return result;
    }

    /**
     * 处理单个触发器
     */
    private boolean processTrigger(ThingsRules thingsRules, ThingsRequestMessage message, ThingsRules.Trigger trigger) {
        ThingsRulesTriggerMatcher matcher = ThingsRulesHandlerFactory.getTriggerMatcher(trigger.getType());
        if (matcher == null) {
            log.warn("No matcher found for trigger type: {}", trigger.getType());
            return false;
        }
        return matcher.match(trigger.getCondition(), message);
    }
}