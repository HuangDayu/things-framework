package cn.huangdayu.things.rules.generator;

import cn.huangdayu.things.common.dsl.rules.ThingsRules;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringJoiner;

/**
 * 条件ID生成器
 * 为规则触发器生成唯一的条件ID，用于规则状态管理
 *
 * @author huangdayu
 */
public class ConditionIdGenerator {

    /**
     * 为触发器生成条件ID
     *
     * @param trigger 触发器对象
     * @return 条件ID
     */
    public static String generateId(ThingsRules.Trigger trigger) {
        if (trigger == null) {
            return "";
        }

        StringJoiner joiner = new StringJoiner("|");
        joiner.add(trigger.getType() != null ? trigger.getType() : "");

        ThingsRules.TriggerCondition condition = trigger.getCondition();
        if (condition != null) {
            joinCondition(joiner, condition);
        }

        return hashString(joiner.toString());
    }

    /**
     * 为触发条件生成条件ID
     *
     * @param condition 触发条件对象
     * @return 条件ID
     */
    public static String generateId(ThingsRules.TriggerCondition condition) {
        if (condition == null) {
            return "";
        }
        StringJoiner joiner = new StringJoiner("|");
        joinCondition(joiner, condition);
        return hashString(joiner.toString());
    }

    private static void joinCondition(StringJoiner joiner, ThingsRules.TriggerCondition condition) {
        joinDeviceInfo(joiner, condition);
        joinBasicConditionFields(joiner, condition);
    }

    private static void joinDeviceInfo(StringJoiner joiner, ThingsRules.TriggerCondition condition) {
        if (condition.getDeviceInfo() != null) {
            ThingsRules.DeviceInfo deviceInfo = condition.getDeviceInfo();
            joiner.add(deviceInfo.getProductCode() != null ? deviceInfo.getProductCode() : "");
            joiner.add(deviceInfo.getDeviceCode() != null ? deviceInfo.getDeviceCode() : "");
            joiner.add(deviceInfo.getMessageType() != null ? deviceInfo.getMessageType() : "");
            joiner.add(deviceInfo.getIdentifier() != null ? deviceInfo.getIdentifier() : "");
            joiner.add(deviceInfo.getAction() != null ? deviceInfo.getAction() : "");
        }
    }

    private static void joinBasicConditionFields(StringJoiner joiner, ThingsRules.TriggerCondition condition) {
        joiner.add(condition.getProperty() != null ? condition.getProperty() : "");
        joiner.add(condition.getOperator() != null ? condition.getOperator() : "");
        joiner.add(condition.getValue() != null ? condition.getValue().toString() : "");
        joiner.add(condition.getEvent() != null ? condition.getEvent() : "");
        joiner.add(condition.getCron() != null ? condition.getCron() : "");
    }

    /**
     * 对字符串进行哈希处理
     *
     * @param input 输入字符串
     * @return 哈希值
     */
    private static String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            return Integer.toString(input.hashCode());
        }
    }

    private static String bytesToHex(byte[] hashBytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 确定触发器类型
     * 根据条件内容确定触发器类型
     *
     * @param condition 触发条件对象
     * @return 触发器类型字符串
     */
    public static String determineTriggerType(ThingsRules.TriggerCondition condition) {
        if (hasExplicitType(condition)) {
            return condition.getType();
        }
        return inferTypeFromCondition(condition);
    }

    private static boolean hasExplicitType(ThingsRules.TriggerCondition condition) {
        return condition.getType() != null && !condition.getType().isEmpty();
    }

    private static String inferTypeFromCondition(ThingsRules.TriggerCondition condition) {
        if (isCompositeCondition(condition)) {
            return "composite";
        }
        if (isTimerCondition(condition)) {
            return "timer";
        }
        if (isEventCondition(condition)) {
            return "event";
        }
        return "device"; // 默认类型
    }

    private static boolean isCompositeCondition(ThingsRules.TriggerCondition condition) {
        return condition.getConditions() != null && !condition.getConditions().isEmpty();
    }

    private static boolean isTimerCondition(ThingsRules.TriggerCondition condition) {
        return condition.getCron() != null;
    }

    private static boolean isEventCondition(ThingsRules.TriggerCondition condition) {
        return condition.getEvent() != null;
    }
}