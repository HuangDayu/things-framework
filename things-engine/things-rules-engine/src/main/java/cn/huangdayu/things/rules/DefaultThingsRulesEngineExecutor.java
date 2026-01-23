package cn.huangdayu.things.rules;

import cn.huangdayu.things.api.rules.*;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.message.ThingsResponseMessage;
import cn.huangdayu.things.rules.state.LocalThingsRulesStateManager;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 规则引擎实现类
 * 负责处理物联网规则的执行，支持跨时间点的条件满足检查
 * 主要功能包括:
 * 1. 触发器匹配与状态管理
 * 2. 执行条件验证
 * 3. 动作执行
 *
 * @author huangdayu
 */
@RequiredArgsConstructor
@Slf4j
@ThingsBean
public class DefaultThingsRulesEngineExecutor implements ThingsRulesEngineExecutor {

    private final ThingsRulesTemplateLoader thingsRulesTemplateLoader;
    private final ThingsRulesSessionManager thingsRulesSessionManager;
    private final ThingsRulesStateManager thingsRulesStateManager;


    @Override
    public ThingsResponseMessage execute(ThingsRequestMessage trm) {
        Set<ThingsRules> rules = thingsRulesTemplateLoader.loader(trm);
        if (CollUtil.isEmpty(rules)) {
            return trm.notFound("Not catch Things Rules");
        }
        List<JSONObject> results = new LinkedList<>();
        executeAllRules(rules, trm, results);
        return trm.success(results);
    }

    private void executeAllRules(Set<ThingsRules> rules, ThingsRequestMessage trm, List<JSONObject> results) {
        for (ThingsRules thingsRules : rules) {
            JSONObject result = executeRuleAndBuildResult(thingsRules, trm);
            results.add(result);
        }
    }

    private JSONObject executeRuleAndBuildResult(ThingsRules thingsRules, ThingsRequestMessage trm) {
        ThingsResponseMessage trm1 = executeRule(thingsRules, trm);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ruleId", thingsRules.getId());
        jsonObject.put(trm1.isSuccess() ? "result" : "error",
                      trm1.isSuccess() ? trm1.getResult() : trm1.getError());
        return jsonObject;
    }

    /**
     * 执行规则
     * 根据传入的消息匹配并执行符合条件的规则
     *
     * @param thingsRules 规则对象
     * @param message     触发规则执行的消息
     * @return 规则执行结果
     */
    @Override
    public ThingsResponseMessage executeRule(ThingsRules thingsRules, ThingsRequestMessage message) {
        try {
            return executeRuleWithValidation(thingsRules, message);
        } catch (Exception e) {
            return handleExecutionError(message, thingsRules, e);
        }
    }

    private ThingsResponseMessage executeRuleWithValidation(ThingsRules thingsRules, ThingsRequestMessage message) {
        ThingsResponseMessage validationResult = validateRuleExecution(thingsRules, message);
        if (validationResult != null) {
            return validationResult;
        }
        return executeActions(thingsRules, message);
    }

    private ThingsResponseMessage validateRuleExecution(ThingsRules thingsRules, ThingsRequestMessage message) {
        if (!isRuleEnabled(thingsRules)) {
            return createDisabledRuleError(thingsRules, message);
        }
        if (!validateExecutionCondition(thingsRules)) {
            return createConditionNotMetError(thingsRules, message);
        }
        return matchTriggers(thingsRules, message) ? null : createTriggerNotMatchedError(thingsRules, message);
    }

    private ThingsResponseMessage createDisabledRuleError(ThingsRules thingsRules, ThingsRequestMessage message) {
        log.warn("Rule is disabled: {}", thingsRules.getId());
        return message.clientError("Rule is disabled");
    }

    private ThingsResponseMessage createConditionNotMetError(ThingsRules thingsRules, ThingsRequestMessage message) {
        log.warn("Execution condition not met for rule: {}", thingsRules.getId());
        return message.clientError("Execution condition not met");
    }

    private ThingsResponseMessage createTriggerNotMatchedError(ThingsRules thingsRules, ThingsRequestMessage message) {
        log.warn("Triggers not matched for rule: {}", thingsRules.getId());
        return message.clientError("Rule processed failed");
    }

    /**
     * 检查规则是否启用
     *
     * @param thingsRules 规则对象
     * @return 规则是否启用
     */
    private boolean isRuleEnabled(ThingsRules thingsRules) {
        return "enabled".equals(thingsRules.getStatus());
    }


    /**
     * 验证执行条件
     *
     * @param thingsRules 规则对象
     * @return 执行条件是否满足
     */
    private boolean validateExecutionCondition(ThingsRules thingsRules) {
        // 如果没有执行条件，则认为条件满足
        if (thingsRules.getExecutionCondition() == null) {
            return true;
        }
        return ThingsRulesHandlerFactory.getAllCheckers()
                .values()
                .stream()
                .allMatch(checker -> checker.check(thingsRules.getExecutionCondition()));
    }


    /**
     * 处理执行异常
     *
     * @param message     消息对象
     * @param thingsRules 规则对象
     * @param e           异常对象
     * @return 带有错误信息的响应对象
     */
    private ThingsResponseMessage handleExecutionError(ThingsRequestMessage message, ThingsRules thingsRules, Exception e) {
        log.error("Error executing rule: {}", thingsRules.getId(), e);
        return message.serverError("Internal error: " + e.getMessage());
    }

    /**
     * 匹配触发器并执行规则
     * 遍历规则的所有触发器，匹配成功后执行规则动作
     *
     * @param thingsRules 规则对象
     * @param message     触发规则执行的消息
     * @return 如果规则执行成功返回true，否则返回false
     */
    private boolean matchTriggers(ThingsRules thingsRules, ThingsRequestMessage message) {
        if (thingsRules.getTriggers() == null || thingsRules.getTriggers().isEmpty()) {
            log.warn("No triggers defined for rule: {}", thingsRules.getId());
            return false;
        }

        return processTriggers(thingsRules, message);
    }

    /**
     * 处理规则的所有触发器
     * 支持跨时间条件：触发器可以在不同时间点被满足
     *
     * @param thingsRules 规则对象
     * @param message     消息对象
     * @return 处理结果
     */
    private boolean processTriggers(ThingsRules thingsRules, ThingsRequestMessage message) {
        if (hasCrossTimeTriggers(thingsRules)) {
            return processCrossTimeTriggers(thingsRules, message);
        }
        return processNormalTriggers(thingsRules, message);
    }

    private boolean processNormalTriggers(ThingsRules thingsRules, ThingsRequestMessage message) {
        for (ThingsRules.Trigger trigger : thingsRules.getTriggers()) {
            if (!processAndLogTrigger(thingsRules, message, trigger)) {
                return false;
            }
        }
        return true;
    }

    private boolean processAndLogTrigger(ThingsRules thingsRules, ThingsRequestMessage message, ThingsRules.Trigger trigger) {
        boolean result = processTrigger(thingsRules, message, trigger);
        log.info("Trigger process [{}:{}] result : {}", thingsRules.getId(), trigger.getType(), result);
        return result;
    }

    /**
     * 检查规则是否包含跨时间条件触发器
     * 如果规则有多个触发器，则认为是跨时间条件
     */
    private boolean hasCrossTimeTriggers(ThingsRules thingsRules) {
        return thingsRules.getTriggers() != null && thingsRules.getTriggers().size() > 1;
    }

    /**
     * 处理跨时间条件的触发器
     */
    private boolean processCrossTimeTriggers(ThingsRules thingsRules, ThingsRequestMessage message) {
        String sessionId = generateCrossTimeSessionId(thingsRules);
        String previousState = loadCrossTimeState(sessionId);
        boolean[] currentSatisfaction = evaluateTriggersForCurrentMessage(thingsRules, message);
        boolean[] accumulatedSatisfaction = accumulateTriggerSatisfaction(previousState, currentSatisfaction, thingsRules);
        return handleCrossTimeCompletion(sessionId, accumulatedSatisfaction);
    }

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
     * 生成跨时间条件的会话ID
     */
    private String generateCrossTimeSessionId(ThingsRules thingsRules) {
        return thingsRules.getId() + "_cross_time";
    }

    /**
     * 加载跨时间条件状态
     */
    private String loadCrossTimeState(String sessionId) {
        LocalThingsRulesStateManager localManager = (LocalThingsRulesStateManager) thingsRulesStateManager;
        return localManager.loadStringState(sessionId);
    }

    /**
     * 检查当前消息满足哪些触发器
     */
    private boolean[] evaluateTriggersForCurrentMessage(ThingsRules thingsRules, ThingsRequestMessage message) {
        int triggerCount = thingsRules.getTriggers().size();
        boolean[] satisfaction = initializeSatisfactionArray(triggerCount);
        evaluateEachTrigger(thingsRules, message, satisfaction);
        return satisfaction;
    }

    private boolean[] initializeSatisfactionArray(int triggerCount) {
        return new boolean[triggerCount];
    }

    private void evaluateEachTrigger(ThingsRules thingsRules, ThingsRequestMessage message, boolean[] satisfaction) {
        for (int i = 0; i < satisfaction.length; i++) {
            evaluateSingleTrigger(thingsRules, message, satisfaction, i);
        }
    }

    private void evaluateSingleTrigger(ThingsRules thingsRules, ThingsRequestMessage message, boolean[] satisfaction, int index) {
        ThingsRules.Trigger trigger = thingsRules.getTriggers().get(index);
        boolean result = processTrigger(thingsRules, message, trigger);
        satisfaction[index] = result;
        log.info("Trigger process [{}:{}] result : {}", thingsRules.getId(), trigger.getType(), result);
    }

    /**
     * 累积触发器满足状态
     */
    private boolean[] accumulateTriggerSatisfaction(String previousState, boolean[] currentSatisfaction, ThingsRules thingsRules) {
        int triggerCount = getTriggerCount(thingsRules);
        boolean[] accumulated = createAccumulatedArray(triggerCount);
        accumulateEachTriggerSatisfaction(previousState, currentSatisfaction, accumulated);
        return accumulated;
    }

    private int getTriggerCount(ThingsRules thingsRules) {
        return thingsRules.getTriggers().size();
    }

    private boolean[] createAccumulatedArray(int triggerCount) {
        return new boolean[triggerCount];
    }

    private void accumulateEachTriggerSatisfaction(String previousState, boolean[] currentSatisfaction, boolean[] accumulated) {
        for (int i = 0; i < accumulated.length; i++) {
            accumulateSingleTriggerSatisfaction(previousState, currentSatisfaction, accumulated, i);
        }
    }

    private void accumulateSingleTriggerSatisfaction(String previousState, boolean[] currentSatisfaction, boolean[] accumulated, int index) {
        boolean previouslySatisfied = isTriggerPreviouslySatisfied(previousState, index);
        accumulated[index] = previouslySatisfied || currentSatisfaction[index];
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
            LocalThingsRulesStateManager localManager = (LocalThingsRulesStateManager) thingsRulesStateManager;
            localManager.saveStringState(sessionId, stateString);
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
        thingsRulesStateManager.clearState(sessionId);
    }



    /**
     * 处理单个触发器
     *
     * @param thingsRules 规则对象
     * @param message     消息对象
     * @param trigger     触发器
     * @return 处理结果
     */
    private boolean processTrigger(ThingsRules thingsRules, ThingsRequestMessage message, ThingsRules.Trigger trigger) {
        ThingsRulesTriggerMatcher matcher = ThingsRulesHandlerFactory.getTriggerMatcher(trigger.getType());
        if (matcher == null) {
            log.warn("No matcher found for trigger type: {}", trigger.getType());
            return false;
        }
        return matcher.match(trigger.getCondition(), message);
    }



    /**
     * 执行规则动作
     * 按顺序执行规则定义的所有动作
     *
     * @param thingsRules 规则对象
     */
    private ThingsResponseMessage executeActions(ThingsRules thingsRules, ThingsRequestMessage message) {
        if (thingsRules.getActions() == null || thingsRules.getActions().isEmpty()) {
            log.warn("No actions defined for rule: {}", thingsRules.getId());
            return message.serverError("No actions defined");
        }
        return processActions(thingsRules, message);
    }

    /**
     * 处理动作列表
     */
    private ThingsResponseMessage processActions(ThingsRules thingsRules, ThingsRequestMessage message) {
        Map<String, AtomicInteger> params = initializeActionResultMap();
        executeAllActions(thingsRules, params);
        return message.success(params);
    }

    private Map<String, AtomicInteger> initializeActionResultMap() {
        return new ConcurrentHashMap<>();
    }

    private void executeAllActions(ThingsRules thingsRules, Map<String, AtomicInteger> params) {
        for (ThingsRules.Action action : thingsRules.getActions()) {
            executeAndRecordAction(thingsRules, action, params);
        }
    }

    private void executeAndRecordAction(ThingsRules thingsRules, ThingsRules.Action action, Map<String, AtomicInteger> params) {
        boolean result = executeAction(action);
        String key = result ? "success" : "failure";
        AtomicInteger counter = params.getOrDefault(key, new AtomicInteger(0));
        counter.incrementAndGet();
        params.put(key, counter);
        log.info("Executed rule [{}] action : {}", thingsRules.getId(), action.getType());
    }

    /**
     * 执行单个动作
     *
     * @param action 动作
     */
    private boolean executeAction(ThingsRules.Action action) {
        ThingsRulesActionExecutor executor = ThingsRulesHandlerFactory.getActionExecutor(action.getType());
        if (executor == null) {
            log.warn("No executor found for action type: {}", action.getType());
            return false;
        }
        return executeActionSafely(action, executor);
    }

    private boolean executeActionSafely(ThingsRules.Action action, ThingsRulesActionExecutor executor) {
        try {
            executor.execute(action.getParams());
            return true;
        } catch (Exception e) {
            log.error("Error executing action: {}", action.getType(), e);
            return false;
        }
    }


}