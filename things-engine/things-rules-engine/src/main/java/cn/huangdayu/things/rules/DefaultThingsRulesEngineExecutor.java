package cn.huangdayu.things.rules;

import cn.huangdayu.things.api.rules.ThingsRulesEngineExecutor;
import cn.huangdayu.things.api.rules.ThingsRulesTemplateLoader;
import cn.huangdayu.things.api.rules.ThingsRulesTriggerProcessor;
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

/**
 * 规则引擎执行器 - 重构后的版本
 * 使用组合和委托模式，遵循SOLID设计原则
 * 主要职责：协调各个组件完成规则执行流程
 * <p>
 * SOLID原则应用：
 * - SRP: 只负责规则执行的协调，不直接处理具体逻辑
 * - OCP: 通过依赖注入，可以轻松扩展新功能
 * - DIP: 依赖抽象接口，不依赖具体实现
 *
 * @author huangdayu
 */
@RequiredArgsConstructor
@Slf4j
@ThingsBean
public class DefaultThingsRulesEngineExecutor implements ThingsRulesEngineExecutor {

    private final ThingsRulesTemplateLoader thingsRulesTemplateLoader;
    private final DefaultThingsRulesValidator defaultThingsRulesValidator;
    private final DefaultThingsRulesActionProcessor defaultThingsRulesActionProcessor;


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
            ThingsResponseMessage response = executeRule(thingsRules, trm);
            JSONObject result = buildRuleResult(thingsRules, response);
            results.add(result);
        }
    }

    /**
     * 执行规则
     * 根据传入的消息匹配并执行符合条件的规则
     * 使用组合模式协调各个组件完成规则执行
     *
     * @param thingsRules 规则对象
     * @param message     触发规则执行的消息
     * @return 规则执行结果
     * @throws IllegalArgumentException 当规则为null时抛出
     */
    @Override
    public ThingsResponseMessage executeRule(ThingsRules thingsRules, ThingsRequestMessage message) {
        if (thingsRules == null) {
            throw new IllegalArgumentException("Rule cannot be null");
        }
        try {
            return executeRuleWithValidation(thingsRules, message);
        } catch (Exception e) {
            return createExecutionError(message, thingsRules, e);
        }
    }

    private ThingsResponseMessage executeRuleWithValidation(ThingsRules thingsRules, ThingsRequestMessage message) {
        // 1. 规则验证
        ThingsResponseMessage validationResult = defaultThingsRulesValidator.validateRuleExecution(thingsRules, message);
        if (validationResult != null) {
            return validationResult;
        }

        // 2. 根据触发器数量选择合适的处理器
        ThingsRulesTriggerProcessor selectedProcessor = selectTriggerProcessor(thingsRules);
        if (!selectedProcessor.matchTriggers(thingsRules, message)) {
            return createTriggerNotMatchedError(thingsRules, message);
        }

        // 3. 动作执行
        return defaultThingsRulesActionProcessor.executeActions(thingsRules, message);
    }

    /**
     * 根据规则的触发器数量选择合适的处理器
     * 多个触发器 = 跨时间处理
     * 单个触发器 = 普通处理
     */
    private ThingsRulesTriggerProcessor selectTriggerProcessor(ThingsRules thingsRules) {
        for (Map.Entry<String, ThingsRulesTriggerProcessor> entry : ThingsRulesHandlerFactory.getAllHandlers(ThingsRulesTriggerProcessor.class).entrySet()) {
            if (entry.getValue().canProcess(thingsRules)) {
                return entry.getValue();
            }
        }
        return ThingsRulesHandlerFactory.getDefaultHandler(ThingsRulesTriggerProcessor.class);
    }


    /**
     * 构建规则执行结果
     */
    public JSONObject buildRuleResult(ThingsRules thingsRules, ThingsResponseMessage responseMessage) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ruleId", thingsRules.getId());
        jsonObject.put(responseMessage.isSuccess() ? "result" : "error",
                responseMessage.isSuccess() ? responseMessage.getResult() : responseMessage.getError());
        return jsonObject;
    }

    /**
     * 创建触发器不匹配错误响应
     */
    public ThingsResponseMessage createTriggerNotMatchedError(ThingsRules thingsRules, ThingsRequestMessage message) {
        return message.clientError("Rule processed failed");
    }

    /**
     * 创建执行错误响应
     */
    public ThingsResponseMessage createExecutionError(ThingsRequestMessage message, ThingsRules thingsRules, Exception e) {
        return message.serverError("Internal error: " + e.getMessage());
    }

}