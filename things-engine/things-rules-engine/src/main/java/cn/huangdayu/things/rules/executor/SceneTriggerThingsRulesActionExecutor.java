package cn.huangdayu.things.rules.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.api.rules.ThingsRulesActionExecutor;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 场景触发动作执行器
 * 负责执行场景触发类型的动作
 * 处理触发预定义场景的逻辑
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class SceneTriggerThingsRulesActionExecutor implements ThingsRulesActionExecutor {

    /**
     * 执行场景触发动作
     * 根据动作参数触发预定义的场景
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

        // 获取场景触发参数
        ThingsRules.SceneTriggerParams sceneTrigger = params.getSceneTrigger();

        // 记录场景触发日志
        logSceneTrigger(sceneTrigger);

        // 返回执行结果
        return getResult(sceneTrigger);
    }

    /**
     * 获取动作类型
     *
     * @return 动作类型字符串
     */
    @Override
    public String getType() {
        return "scene_trigger";
    }

    /**
     * 检查参数是否无效
     *
     * @param params 动作参数
     * @return 如果参数无效返回true，否则返回false
     */
    private boolean isInvalidParams(ThingsRules.ActionParams params) {
        return params == null || params.getSceneTrigger() == null;
    }

    /**
     * 记录场景触发日志
     *
     * @param sceneTrigger 场景触发参数
     */
    private void logSceneTrigger(ThingsRules.SceneTriggerParams sceneTrigger) {
        log.info("Scene trigger action triggered: {}", sceneTrigger);
    }

    /**
     * 获取执行结果
     *
     * @param sceneTrigger 场景触发参数
     * @return 执行结果
     */
    private JSONObject getResult(ThingsRules.SceneTriggerParams sceneTrigger) {
        // 在实际项目中，这里会触发场景
        // 简化实现，直接返回场景触发信息
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "Scene trigger action executed");
        result.put("sceneId", sceneTrigger.getSceneId());
        return result;
    }
}