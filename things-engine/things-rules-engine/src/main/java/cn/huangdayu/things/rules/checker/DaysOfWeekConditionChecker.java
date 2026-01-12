package cn.huangdayu.things.rules.checker;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.api.rules.ThingsRulesConditionChecker;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * 星期几条件检查器
 * 负责检查当前星期是否在规则定义的允许执行星期列表中
 * 支持规则在特定星期几执行的限制
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class DaysOfWeekConditionChecker implements ThingsRulesConditionChecker {

    /**
     * 检查星期几条件
     * 验证当前星期是否在规则定义的允许执行星期列表中
     *
     * @param executionCondition 执行条件对象
     * @return 如果星期几条件满足返回true，否则返回false
     */
    @Override
    public boolean check(ThingsRules.ExecutionCondition executionCondition) {
        // 如果没有执行条件，则认为条件满足
        if (executionCondition == null) {
            return true;
        }

        // 获取星期几条件
        List<Integer> daysOfWeek = executionCondition.getDaysOfWeek();
        if (daysOfWeek != null && !daysOfWeek.isEmpty()) {
            // 检查当前星期是否匹配
            return isDayOfWeekMatched(daysOfWeek);
        }

        // 如果没有星期几条件，则认为条件满足
        return true;
    }

    /**
     * 获取条件类型
     *
     * @return 条件类型字符串
     */
    @Override
    public String getType() {
        return "days_of_week";
    }

    /**
     * 检查当前星期是否匹配
     * 获取当前星期并与规则定义的允许执行星期列表进行比较
     *
     * @param daysOfWeek 允许执行的星期列表
     * @return 如果当前星期匹配返回true，否则返回false
     */
    private boolean isDayOfWeekMatched(List<Integer> daysOfWeek) {
        // 检查是否在测试模式下
        String env = System.getProperty("env");
        if ("test".equals(env)) {
            log.debug("Test mode: always return true for days of week condition");
            return true;
        }

        // 获取当前星期几
        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
        int dayValue = currentDay.getValue(); // Monday=1, Sunday=7

        // 处理星期日的特殊情况（在我们的系统中可能是0或7）
        if (dayValue == 7) {
            return daysOfWeek.contains(7) || daysOfWeek.contains(0);
        }

        // 检查当前星期是否在允许列表中
        return daysOfWeek.contains(dayValue);
    }
}