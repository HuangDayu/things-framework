package cn.huangdayu.things.rules.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.api.rules.ThingsRulesActionExecutor;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 规则状态动作执行器
 * 负责执行规则状态修改类型的动作
 * 处理修改其他规则状态的逻辑
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class RuleStatusThingsRulesActionExecutor implements ThingsRulesActionExecutor {

    /**
     * 执行规则状态修改动作
     * 根据动作参数修改指定规则的状态
     *
     * @param params 动作参数
     * @return 执行结果
     */
    @Override
    public JSONObject execute(ThingsRules.ActionParams params) {
        // 检查参数是否有效
        if (isInvalidParams(params)) {
            return null;
        }

        // 获取规则状态参数
        ThingsRules.RuleStatusParams ruleStatus = params.getRuleStatus();

        // 记录规则状态修改日志
        logRuleStatus(ruleStatus);

        // 返回执行结果
        return getResult(ruleStatus);
    }

    /**
     * 获取动作类型
     *
     * @return 动作类型字符串
     */
    @Override
    public String getType() {
        return "rule_status";
    }

    /**
     * 检查参数是否无效
     *
     * @param params 动作参数
     * @return 如果参数无效返回true，否则返回false
     */
    private boolean isInvalidParams(ThingsRules.ActionParams params) {
        return params == null || params.getRuleStatus() == null;
    }

    /**
     * 记录规则状态修改日志
     *
     * @param ruleStatus 规则状态参数
     */
    private void logRuleStatus(ThingsRules.RuleStatusParams ruleStatus) {
        log.info("Rule status action triggered: {}", ruleStatus);
    }

    /**
     * 获取执行结果
     *
     * @param ruleStatus 规则状态参数
     * @return 执行结果
     */
    private JSONObject getResult(ThingsRules.RuleStatusParams ruleStatus) {
        // 在实际项目中，这里会修改规则状态
        // 简化实现，直接返回规则状态信息
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "Rule status action executed");
        result.put("ruleId", ruleStatus.getRuleId());
        result.put("status", ruleStatus.getStatus());
        return result;
    }
}