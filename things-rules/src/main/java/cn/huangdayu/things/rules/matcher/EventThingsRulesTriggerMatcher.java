package cn.huangdayu.things.rules.matcher;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.api.rules.ThingsRulesTriggerMatcher;
import lombok.extern.slf4j.Slf4j;

/**
 * 事件触发器匹配器
 * 负责匹配设备事件类型的触发器
 * 处理设备事件上报消息与规则定义的触发条件之间的匹配逻辑
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class EventThingsRulesTriggerMatcher implements ThingsRulesTriggerMatcher {

    /**
     * 匹配事件触发器条件
     * 检查传入的消息是否满足事件触发器定义的条件
     *
     * @param condition 触发条件对象
     * @param message   触发规则执行的消息
     * @return 如果匹配成功返回true，否则返回false
     */
    @Override
    public boolean match(ThingsRules.TriggerCondition condition, ThingsRequestMessage message) {
        // 检查事件触发器是否有效
        if (isInvalidEventTrigger(condition)) {
            return false;
        }

        // 检查方法是否匹配
        if (!isMethodMatched(condition, message)) {
            return false;
        }

        // 检查事件是否匹配
        return isEventMatched(condition, message);
    }

    /**
     * 获取触发器类型
     *
     * @return 触发器类型字符串
     */
    @Override
    public String getType() {
        return "event";
    }

    /**
     * 检查事件触发器是否无效
     * 验证事件触发器的必要字段是否都已设置
     *
     * @param condition 触发条件对象
     * @return 如果触发器无效返回true，否则返回false
     */
    private boolean isInvalidEventTrigger(ThingsRules.TriggerCondition condition) {
        return checkDeviceInfoNull(condition) || checkEventNull(condition);
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
     * 检查事件是否为空
     *
     * @param condition 触发条件对象
     * @return 如果事件为空返回true，否则返回false
     */
    private boolean checkEventNull(ThingsRules.TriggerCondition condition) {
        return condition.getEvent() == null;
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
        String expectedMethod = buildExpectedMethod(deviceInfo);

        // 比较方法字符串
        return expectedMethod.equals(message.getMethod());
    }

    /**
     * 构建期望的方法字符串
     * 根据设备信息构建预期的方法字符串
     *
     * @param deviceInfo 设备信息对象
     * @return 返回构建好的方法字符串
     */
    private String buildExpectedMethod(ThingsRules.DeviceInfo deviceInfo) {
        return String.format("%s/%s/%s/%s/%s",
                deviceInfo.getProductCode(),
                deviceInfo.getDeviceCode(),
                deviceInfo.getMessageType(),
                deviceInfo.getIdentifier(),
                deviceInfo.getAction());
    }

    /**
     * 检查事件是否匹配
     * 验证消息中的事件是否与触发条件中定义的事件匹配
     *
     * @param condition 触发条件对象
     * @param message   触发规则执行的消息
     * @return 如果事件匹配返回true，否则返回false
     */
    private boolean isEventMatched(ThingsRules.TriggerCondition condition, ThingsRequestMessage message) {
        String eventValue = getEventValueFromMessage(message);
        if (eventValue == null) {
            return false;
        }
        return condition.getEvent().equals(eventValue);
    }

    /**
     * 从消息中获取事件值
     * 提取消息参数中的事件值
     *
     * @param message 触发规则执行的消息
     * @return 返回事件值字符串，如果不存在返回null
     */
    private String getEventValueFromMessage(ThingsRequestMessage message) {
        Object params = message.getParams();
        if (params instanceof java.util.Map) {
            Object event = ((java.util.Map<?, ?>) params).get("event");
            return event != null ? event.toString() : null;
        }
        return null;
    }
}