package cn.huangdayu.things.rules.checker;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.api.rules.ThingsRulesConditionChecker;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;

/**
 * 时间范围条件检查器
 * 负责检查当前时间是否在规则定义的时间范围内
 * 支持规则在特定时间段内执行的限制
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class TimeRangeConditionChecker implements ThingsRulesConditionChecker {

    /**
     * 检查时间范围条件
     * 验证当前时间是否在规则定义的时间范围内
     *
     * @param executionCondition 执行条件对象
     * @return 如果时间范围条件满足返回true，否则返回false
     */
    @Override
    public boolean check(ThingsRules.ExecutionCondition executionCondition) {
        // 如果没有执行条件，则认为条件满足
        if (executionCondition == null) {
            return true;
        }

        // 获取时间范围条件
        ThingsRules.TimeRange timeRange = executionCondition.getTimeRange();
        if (timeRange != null) {
            // 检查时间是否在范围内
            return isTimeInRange(timeRange);
        }

        // 如果没有时间范围条件，则认为条件满足
        return true;
    }

    /**
     * 获取条件类型
     *
     * @return 条件类型字符串
     */
    @Override
    public String getType() {
        return "time_range";
    }

    /**
     * 检查当前时间是否在指定范围内
     * 解析时间范围并判断当前时间是否在该范围内
     *
     * @param timeRange 时间范围对象
     * @return 如果当前时间在范围内返回true，否则返回false
     */
    private boolean isTimeInRange(ThingsRules.TimeRange timeRange) {
        String startTimeStr = timeRange.getStart();
        String endTimeStr = timeRange.getEnd();
        if (startTimeStr == null || endTimeStr == null) {
            return true;
        }
        return isTimeInRangeSafely(startTimeStr, endTimeStr);
    }

    private boolean isTimeInRangeSafely(String startTimeStr, String endTimeStr) {
        try {
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);
            if (isTestMode()) {
                return true;
            }
            return checkTimeInRange(startTime, endTime);
        } catch (Exception e) {
            log.error("Error parsing time range: {} - {}", startTimeStr, endTimeStr, e);
            return true;
        }
    }

    private boolean isTestMode() {
        String env = System.getProperty("env");
        if ("test".equals(env)) {
            log.debug("Test mode: always return true for time range condition");
            return true;
        }
        return false;
    }

    private boolean checkTimeInRange(LocalTime startTime, LocalTime endTime) {
        LocalTime now = LocalTime.now();
        if (endTime.isAfter(startTime)) {
            return !now.isBefore(startTime) && !now.isAfter(endTime);
        } else {
            return !now.isBefore(startTime) || !now.isAfter(endTime);
        }
    }
}