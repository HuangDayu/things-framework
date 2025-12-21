package cn.huangdayu.things.rules.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.api.rules.ThingsRulesActionExecutor;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据转发动作执行器
 * 负责执行数据转发类型的动作
 * 处理将数据转发到其他系统或服务的逻辑
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class DataForwardThingsRulesActionExecutor implements ThingsRulesActionExecutor {

    /**
     * 执行数据转发动作
     * 根据动作参数转发数据到指定目标
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

        // 获取数据转发参数
        ThingsRules.DataForwardParams dataForward = params.getDataForward();

        // 记录数据转发日志
        logDataForward(dataForward);

        // 返回执行结果
        return getResult(dataForward);
    }

    /**
     * 获取动作类型
     *
     * @return 动作类型字符串
     */
    @Override
    public String getType() {
        return "data_forward";
    }

    /**
     * 检查参数是否无效
     *
     * @param params 动作参数
     * @return 如果参数无效返回true，否则返回false
     */
    private boolean isInvalidParams(ThingsRules.ActionParams params) {
        return params == null || params.getDataForward() == null;
    }

    /**
     * 记录数据转发日志
     *
     * @param dataForward 数据转发参数
     */
    private void logDataForward(ThingsRules.DataForwardParams dataForward) {
        log.info("Data forward action triggered: {}", dataForward);
    }

    /**
     * 获取执行结果
     *
     * @param dataForward 数据转发参数
     * @return 执行结果
     */
    private JSONObject getResult(ThingsRules.DataForwardParams dataForward) {
        // 在实际项目中，这里会执行数据转发逻辑
        // 简化实现，直接返回数据转发信息
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "Data forward action executed");
        result.put("targetUrl", dataForward.getTargetUrl());
        result.put("data", dataForward.getData());
        return result;
    }
}