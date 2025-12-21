package cn.huangdayu.things.rules.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.api.rules.ThingsRulesActionExecutor;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 延时动作执行器
 * 负责执行延时类型的动作
 * 处理规则执行中的延时逻辑
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class DelayThingsRulesActionExecutor implements ThingsRulesActionExecutor {

    /**
     * 执行延时动作
     * 根据动作参数执行延时操作
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

        // 获取延时参数
        ThingsRules.DelayParams delay = params.getDelay();

        // 记录延时日志
        logDelay(delay);

        // 返回执行结果
        return getResult(delay);
    }

    /**
     * 获取动作类型
     *
     * @return 动作类型字符串
     */
    @Override
    public String getType() {
        return "delay";
    }

    /**
     * 检查参数是否无效
     *
     * @param params 动作参数
     * @return 如果参数无效返回true，否则返回false
     */
    private boolean isInvalidParams(ThingsRules.ActionParams params) {
        return params == null || params.getDelay() == null;
    }

    /**
     * 记录延时日志
     *
     * @param delay 延时参数
     */
    private void logDelay(ThingsRules.DelayParams delay) {
        log.info("Delay action triggered: {} seconds", delay.getDuration());
    }

    /**
     * 获取执行结果
     *
     * @param delay 延时参数
     * @return 执行结果
     */
    private JSONObject getResult(ThingsRules.DelayParams delay) {
        // 在实际项目中，这里会执行延时逻辑
        // 简化实现，直接返回延时信息
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "Delay action executed");
        result.put("duration", delay.getDuration());
        return result;
    }
}