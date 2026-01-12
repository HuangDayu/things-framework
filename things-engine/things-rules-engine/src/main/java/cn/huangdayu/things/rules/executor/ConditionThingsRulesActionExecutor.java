package cn.huangdayu.things.rules.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.api.rules.ThingsRulesActionExecutor;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 条件动作执行器
 * 负责执行条件类型的动作
 * 处理根据条件执行不同动作的逻辑
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class ConditionThingsRulesActionExecutor implements ThingsRulesActionExecutor {

    /**
     * 执行条件动作
     * 根据动作参数中的条件表达式执行相应的逻辑
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

        // 获取条件参数
        ThingsRules.ConditionParams condition = params.getCondition();

        // 记录条件动作日志
        logCondition(condition);

        // 返回执行结果
        return getResult(condition);
    }

    /**
     * 获取动作类型
     *
     * @return 动作类型字符串
     */
    @Override
    public String getType() {
        return "condition";
    }

    /**
     * 检查参数是否无效
     *
     * @param params 动作参数
     * @return 如果参数无效返回true，否则返回false
     */
    private boolean isInvalidParams(ThingsRules.ActionParams params) {
        return params == null || params.getCondition() == null;
    }

    /**
     * 记录条件动作日志
     *
     * @param condition 条件参数
     */
    private void logCondition(ThingsRules.ConditionParams condition) {
        log.info("Condition action triggered: {}", condition);
    }

    /**
     * 获取执行结果
     *
     * @param condition 条件参数
     * @return 执行结果
     */
    private JSONObject getResult(ThingsRules.ConditionParams condition) {
        // 在实际项目中，这里会根据条件执行不同的动作
        // 简化实现，直接返回条件信息
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "Condition action executed");
        result.put("expression", condition.getExpression());
        return result;
    }
}