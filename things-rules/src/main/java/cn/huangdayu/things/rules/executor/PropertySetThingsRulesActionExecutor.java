package cn.huangdayu.things.rules.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.api.rules.ThingsRulesActionExecutor;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 属性设置动作执行器
 * 负责执行属性设置类型的动作
 * 处理设备属性设置的逻辑
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class PropertySetThingsRulesActionExecutor implements ThingsRulesActionExecutor {

    /**
     * 执行属性设置动作
     * 根据动作参数设置设备属性
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

        // 获取属性设置参数
        ThingsRules.PropertySetParams propertySet = params.getPropertySet();

        // 记录属性设置日志
        logPropertySet(propertySet);

        // 返回执行结果
        return getResult(propertySet);
    }

    /**
     * 获取动作类型
     *
     * @return 动作类型字符串
     */
    @Override
    public String getType() {
        return "property_set";
    }

    /**
     * 检查参数是否无效
     *
     * @param params 动作参数
     * @return 如果参数无效返回true，否则返回false
     */
    private boolean isInvalidParams(ThingsRules.ActionParams params) {
        return params == null || params.getPropertySet() == null;
    }

    /**
     * 记录属性设置日志
     *
     * @param propertySet 属性设置参数
     */
    private void logPropertySet(ThingsRules.PropertySetParams propertySet) {
        log.info("Property set action triggered: {}", propertySet);
    }

    /**
     * 获取执行结果
     *
     * @param propertySet 属性设置参数
     * @return 执行结果
     */
    private JSONObject getResult(ThingsRules.PropertySetParams propertySet) {
        // 在实际项目中，这里会设置属性值
        // 简化实现，直接返回属性设置信息
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "Property set action executed");
        result.put("targetDevice", propertySet.getTargetDevice());
        result.put("properties", propertySet.getProperties());
        return result;
    }
}