package cn.huangdayu.things.rules.matcher;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.rules.ThingsRulesHandlerFactory;
import cn.huangdayu.things.api.rules.ThingsRulesPropertyComparator;
import cn.huangdayu.things.api.rules.ThingsRulesTriggerMatcher;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 设备触发器匹配器
 * 负责匹配设备属性变化类型的触发器
 * 处理设备属性上报消息与规则定义的触发条件之间的匹配逻辑
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class DeviceThingsRulesTriggerMatcher implements ThingsRulesTriggerMatcher {

    /**
     * 匹配设备触发器条件
     * 检查传入的消息是否满足设备触发器定义的条件
     *
     * @param condition 触发条件对象
     * @param message   触发规则执行的消息
     * @return 如果匹配成功返回true，否则返回false
     */
    @Override
    public boolean match(ThingsRules.TriggerCondition condition, ThingsRequestMessage message) {
        // 检查设备触发器是否有效
        if (isInvalidDeviceTrigger(condition)) {
            return false;
        }

        // 检查方法是否匹配
        if (!isMethodMatched(condition, message)) {
            return false;
        }

        // 检查属性是否匹配
        return isPropertyMatched(condition, message);
    }

    /**
     * 获取触发器类型
     *
     * @return 触发器类型字符串
     */
    @Override
    public String getType() {
        return "device";
    }

    /**
     * 检查设备触发器是否无效
     * 验证设备触发器的必要字段是否都已设置
     *
     * @param condition 触发条件对象
     * @return 如果触发器无效返回true，否则返回false
     */
    private boolean isInvalidDeviceTrigger(ThingsRules.TriggerCondition condition) {
        return checkDeviceInfoNull(condition) ||
                checkPropertyNull(condition) ||
                checkOperatorNull(condition) ||
                checkValueNull(condition);
    }

    /**
     * 检查设备信息是否为空
     *
     * @param condition 触发条件对象
     * @return 如果设备信息为空返回true，否则返回false
     */
    private boolean checkDeviceInfoNull(ThingsRules.TriggerCondition condition) {
        return condition.getDeviceInfo() == null;
    }

    /**
     * 检查属性是否为空
     *
     * @param condition 触发条件对象
     * @return 如果属性为空返回true，否则返回false
     */
    private boolean checkPropertyNull(ThingsRules.TriggerCondition condition) {
        return condition.getProperty() == null;
    }

    /**
     * 检查操作符是否为空
     *
     * @param condition 触发条件对象
     * @return 如果操作符为空返回true，否则返回false
     */
    private boolean checkOperatorNull(ThingsRules.TriggerCondition condition) {
        return condition.getOperator() == null;
    }

    /**
     * 检查值是否为空
     *
     * @param condition 触发条件对象
     * @return 如果值为空返回true，否则返回false
     */
    private boolean checkValueNull(ThingsRules.TriggerCondition condition) {
        return condition.getValue() == null;
    }

    /**
     * 检查方法是否匹配
     * 验证消息的方法是否与触发条件中定义的设备信息匹配
     *
     * @param condition 触发条件对象
     * @param message   触发规则执行的消息
     * @return 如果方法匹配返回true，否则返回false
     */
    private boolean isMethodMatched(ThingsRules.TriggerCondition condition, ThingsRequestMessage message) {
        ThingsRules.DeviceInfo deviceInfo = condition.getDeviceInfo();

        // 构建期望的方法字符串
        String expectedMethod = String.format("%s/%s/%s/%s/%s",
                deviceInfo.getProductCode(),
                deviceInfo.getDeviceCode(),
                deviceInfo.getMessageType(),
                deviceInfo.getIdentifier(),
                deviceInfo.getAction());

        // 比较方法字符串
        return expectedMethod.equals(message.getMethod());
    }

    /**
     * 检查属性是否匹配
     * 验证消息中的属性值是否满足触发条件中定义的比较条件
     *
     * @param condition 触发条件对象
     * @param message   触发规则执行的消息
     * @return 如果属性匹配返回true，否则返回false
     */
    private boolean isPropertyMatched(ThingsRules.TriggerCondition condition, ThingsRequestMessage message) {
        // 获取消息参数
        Map<String, Object> params = message.getParams();
        if (params == null) {
            return false;
        }

        // 获取属性值
        Object propertyValue = params.get(condition.getProperty());
        if (propertyValue == null) {
            return false;
        }

        // 使用属性比较器工厂获取对应的比较器
        ThingsRulesPropertyComparator comparator = ThingsRulesHandlerFactory.getComparator(condition.getOperator());
        if (comparator == null) {
            log.warn("Unsupported operator: {}", condition.getOperator());
            return false;
        }

        // 执行属性比较
        return comparator.compare(condition, propertyValue);
    }
}