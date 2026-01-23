package cn.huangdayu.things.rules.processor;

import cn.huangdayu.things.api.rules.ThingsRulesStateManager;
import cn.huangdayu.things.api.rules.ThingsRulesTriggerMatcher;
import cn.huangdayu.things.api.rules.ThingsRulesTriggerProcessor;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.rules.ThingsRulesHandlerFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 本地跨时间触发器处理器实现
 * 使用本地内存/文件存储跨时间触发器状态
 * 适合单机部署或小型系统
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class LocalCrossTimeTriggerProcessor implements ThingsRulesTriggerProcessor {

    private static ThingsRulesStateManager getThingsRulesStateManager() {
        return ThingsRulesHandlerFactory.getHandler(ThingsRulesStateManager.class);
    }

    @Override
    public boolean canProcess(ThingsRules thingsRules) {
        return thingsRules.getTriggers() != null && thingsRules.getTriggers().size() > 1;
    }

    @Override
    public boolean matchTriggers(ThingsRules thingsRules, ThingsRequestMessage message) {
        String sessionId = generateCrossTimeSessionId(thingsRules);
        String previousState = loadCrossTimeState(sessionId);
        boolean[] currentSatisfaction = evaluateTriggersForCurrentMessage(thingsRules, message);
        boolean[] accumulatedSatisfaction = accumulateTriggerSatisfaction(previousState, currentSatisfaction, thingsRules);
        return handleCrossTimeCompletion(sessionId, accumulatedSatisfaction);
    }

    @Override
    public String getType() {
        return "local_cross_time_trigger_processor";
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    /**
     * 生成跨时间条件的会话ID
     */
    private String generateCrossTimeSessionId(ThingsRules thingsRules) {
        return thingsRules.getId() + "_cross_time";
    }

    /**
     * 加载跨时间条件状态
     */
    private String loadCrossTimeState(String sessionId) {
        return getThingsRulesStateManager().loadStringState(sessionId);
    }

    /**
     * 检查当前消息满足哪些触发器
     */
    private boolean[] evaluateTriggersForCurrentMessage(ThingsRules thingsRules, ThingsRequestMessage message) {
        int triggerCount = thingsRules.getTriggers().size();
        boolean[] satisfaction = new boolean[triggerCount];
        for (int i = 0; i < satisfaction.length; i++) {
            ThingsRules.Trigger trigger = thingsRules.getTriggers().get(i);
            boolean result = processTrigger(thingsRules, message, trigger);
            satisfaction[i] = result;
            log.info("Trigger process [{}:{}] result : {}", thingsRules.getId(), trigger.getType(), result);
        }
        return satisfaction;
    }

    /**
     * 累积触发器满足状态
     */
    private boolean[] accumulateTriggerSatisfaction(String previousState, boolean[] currentSatisfaction, ThingsRules thingsRules) {
        int triggerCount = thingsRules.getTriggers().size();
        boolean[] accumulated = new boolean[triggerCount];
        for (int i = 0; i < accumulated.length; i++) {
            boolean previouslySatisfied = isTriggerPreviouslySatisfied(previousState, i);
            accumulated[i] = previouslySatisfied || currentSatisfaction[i];
        }
        return accumulated;
    }

    /**
     * 检查触发器是否之前已经满足
     */
    private boolean isTriggerPreviouslySatisfied(String previousState, int triggerIndex) {
        if (previousState == null || previousState.isEmpty()) {
            return false;
        }
        return previousState.contains("trigger_" + triggerIndex + ":");
    }

    /**
     * 处理跨时间完成逻辑
     */
    private boolean handleCrossTimeCompletion(String sessionId, boolean[] accumulatedSatisfaction) {
        if (areAllTriggersSatisfied(accumulatedSatisfaction)) {
            clearCrossTimeState(sessionId);
            return true;
        } else {
            saveCrossTimeState(sessionId, accumulatedSatisfaction);
            return false;
        }
    }

    /**
     * 检查是否所有触发器都已满足
     */
    private boolean areAllTriggersSatisfied(boolean[] satisfaction) {
        for (boolean satisfied : satisfaction) {
            if (!satisfied) {
                return false;
            }
        }
        return true;
    }

    /**
     * 保存跨时间条件状态
     */
    private void saveCrossTimeState(String sessionId, boolean[] satisfaction) {
        String stateString = buildStateString(satisfaction);
        if (!stateString.isEmpty()) {
            getThingsRulesStateManager().saveStringState(sessionId, stateString);
        }
    }

    /**
     * 构建状态字符串
     */
    private String buildStateString(boolean[] satisfaction) {
        StringBuilder state = new StringBuilder();
        for (int i = 0; i < satisfaction.length; i++) {
            if (satisfaction[i]) {
                state.append("trigger_").append(i).append(":");
            }
        }
        return state.toString();
    }

    /**
     * 清除跨时间条件状态
     */
    private void clearCrossTimeState(String sessionId) {
        getThingsRulesStateManager().clearState(sessionId);
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