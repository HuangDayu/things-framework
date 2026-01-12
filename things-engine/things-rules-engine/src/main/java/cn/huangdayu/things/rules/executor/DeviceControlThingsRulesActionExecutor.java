package cn.huangdayu.things.rules.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.api.rules.ThingsRulesActionExecutor;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 设备控制动作执行器
 * 负责执行设备控制类型的动作
 * 处理向设备发送控制指令的逻辑
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class DeviceControlThingsRulesActionExecutor implements ThingsRulesActionExecutor {

    /**
     * 执行设备控制动作
     * 根据动作参数向目标设备发送控制指令
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

        // 获取设备控制参数
        ThingsRules.DeviceControlParams deviceControl = params.getDeviceControl();

        // 执行设备控制
        return executeDeviceControl(deviceControl);
    }

    /**
     * 获取动作类型
     *
     * @return 动作类型字符串
     */
    @Override
    public String getType() {
        return "device_control";
    }

    /**
     * 检查参数是否无效
     *
     * @param params 动作参数
     * @return 如果参数无效返回true，否则返回false
     */
    private boolean isInvalidParams(ThingsRules.ActionParams params) {
        return params == null || params.getDeviceControl() == null;
    }

    /**
     * 执行设备控制
     * 实际执行设备控制逻辑
     *
     * @param deviceControl 设备控制参数
     * @return 执行结果
     */
    private JSONObject executeDeviceControl(ThingsRules.DeviceControlParams deviceControl) {
        try {
            log.info("Executing device control action: {}", deviceControl);

            // 在实际项目中，这里会发送设备控制消息
            // 模拟设备控制执行结果
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("message", "Device control executed successfully");
            result.put("targetDevice", deviceControl.getTargetDevice());
            result.put("service", deviceControl.getService());
            result.put("params", deviceControl.getParams());

            return result;
        } catch (Exception e) {
            log.error("Error executing device control action: {}", deviceControl, e);
            JSONObject errorResult = new JSONObject();
            errorResult.put("success", false);
            errorResult.put("message", "Error executing device control action: " + e.getMessage());
            return errorResult;
        }
    }
}