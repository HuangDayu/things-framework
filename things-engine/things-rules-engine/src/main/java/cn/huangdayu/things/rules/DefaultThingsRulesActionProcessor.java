package cn.huangdayu.things.rules;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.api.rules.ThingsRulesActionExecutor;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.message.ThingsResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 动作处理器
 * 负责执行规则动作的逻辑
 * 遵循单一职责原则，只负责动作执行
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
@RequiredArgsConstructor
public class DefaultThingsRulesActionProcessor {

    /**
     * 执行规则动作
     */
    public ThingsResponseMessage executeActions(ThingsRules thingsRules, ThingsRequestMessage message) {
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
        Map<String, AtomicInteger> resultMap = new ConcurrentHashMap<>();
        for (ThingsRules.Action action : thingsRules.getActions()) {
            executeAndRecordAction(thingsRules, action, resultMap);
        }
        return message.success(resultMap);
    }

    /**
     * 执行单个动作并记录结果
     */
    private void executeAndRecordAction(ThingsRules thingsRules, ThingsRules.Action action, Map<String, AtomicInteger> resultMap) {
        boolean result = executeAction(action);
        String key = result ? "success" : "failure";
        AtomicInteger counter = resultMap.getOrDefault(key, new AtomicInteger(0));
        counter.incrementAndGet();
        resultMap.put(key, counter);
        log.info("Executed rule [{}] action : {}", thingsRules.getId(), action.getType());
    }

    /**
     * 执行单个动作
     */
    private boolean executeAction(ThingsRules.Action action) {
        ThingsRulesActionExecutor executor = ThingsRulesHandlerFactory.getActionExecutor(action.getType());
        if (executor == null) {
            log.warn("No executor found for action type: {}", action.getType());
            return false;
        }
        return executeActionSafely(action, executor);
    }

    /**
     * 安全执行动作
     */
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