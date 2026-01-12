package cn.huangdayu.things.rules;

import cn.huangdayu.things.api.rules.*;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.message.ThingsResponseMessage;
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
        Set<ThingsRules> loader = thingsRulesTemplateLoader.loader(trm);
        if (CollUtil.isEmpty(loader)) {
            return trm.notFound("Not catch Things Rules");
        }
        List<JSONObject> re = new LinkedList<>();
        for (ThingsRules thingsRules : loader) {
            ThingsResponseMessage trm1 = executeRule(thingsRules, trm);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ruleId", thingsRules.getId());
            jsonObject.put(trm1.isSuccess() ? "result" : "error", trm1.isSuccess() ? trm1.getResult() : trm1.getError());
            re.add(jsonObject);
        }
        return trm.success(re);

        // FIXME: 待完善

//        // 加载状态
//        Facts sessionFacts = loadOrCreateSessionFacts(sessionId, facts);
//
//        // 加载规则
//        List<Rule> rules = ruleLoader.loadRules();
//        metrics.setLoadedRulesCount(rules.size());
//
//        // 评估规则
//        List<Rule> triggeredRules = evaluateRules(rules, sessionFacts);
//
//        // 解决冲突
//        List<Rule> resolvedRules = conflictResolver.resolve(triggeredRules);
//
//        // 执行动作
//        executeActions(resolvedRules, sessionFacts);
//
//        // 保存状态
//        stateManager.saveState(sessionId, sessionFacts);
//
//        // 返回结果
//        return ExecutionResult.success(resolvedRules);

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
            if (!isRuleEnabled(thingsRules)) {
                log.warn("Rule is disabled: {}", thingsRules.getId());
                return message.clientError("Rule is disabled");
            }
            if (!validateExecutionCondition(thingsRules)) {
                log.warn("Execution condition not met for rule: {}", thingsRules.getId());
                return message.clientError("Execution condition not met");
            }
            if (!matchTriggers(thingsRules, message)) {
                log.warn("Triggers not matched for rule: {}", thingsRules.getId());
                return message.clientError("Rule processed failed");
            }

            return executeActions(thingsRules, message);
        } catch (Exception e) {
            return handleExecutionError(message, thingsRules, e);
        }
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
     *
     * @param thingsRules 规则对象
     * @param message     消息对象
     * @return 处理结果
     */
    private boolean processTriggers(ThingsRules thingsRules, ThingsRequestMessage message) {
        for (ThingsRules.Trigger trigger : thingsRules.getTriggers()) {
            boolean result = processTrigger(thingsRules, message, trigger);
            log.info("Trigger process [{}:{}] result : {}", thingsRules.getId(), trigger.getType(), result);
            if (!result) {
                return false;
            }
        }
        return true;
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
     *
     * @param thingsRules 规则对象
     */
    private ThingsResponseMessage processActions(ThingsRules thingsRules, ThingsRequestMessage message) {
        Map<String, AtomicInteger> params = new ConcurrentHashMap<>();
        for (ThingsRules.Action action : thingsRules.getActions()) {
            boolean result = executeAction(action);
            String key = result ? "success" : "failure";
            AtomicInteger atomicInteger = params.getOrDefault(key, new AtomicInteger(0));
            atomicInteger.incrementAndGet();
            params.put(key, atomicInteger);
            log.info("Executed rule [{}] action : {}", thingsRules.getId(), action.getType());
        }
        return message.success(params);
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

        try {
            executor.execute(action.getParams());
        } catch (Exception e) {
            log.error("Error executing action: {}", action.getType(), e);
            return false;
        }
        return true;
    }


}