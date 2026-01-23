package cn.huangdayu.things.rules.checker;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.api.rules.ThingsRulesConditionChecker;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行次数限制条件检查器
 * 负责检查规则在特定时间窗口内的执行次数是否超过限制
 * 支持规则执行频率的限制
 * <p>
 * 该检查器实现以下功能：
 * 1. 解析规则中定义的执行次数限制
 * 2. 跟踪规则的执行历史
 * 3. 根据时间窗口清理过期的执行记录
 * 4. 判断当前执行是否超过预设的次数限制
 * <p>
 * 注意：该实现使用内存存储执行记录，适用于单机部署场景
 * 在分布式环境中建议使用Redis等集中式存储
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class ExecutionLimitConditionChecker implements ThingsRulesConditionChecker {

    /**
     * 执行记录映射
     * key: 规则ID（唯一标识规则的字符串）
     * value: 执行记录对象
     * 简单的内存存储，实际项目中应该使用Redis或数据库存储
     */
    private static final Map<String, ExecutionRecord> EXECUTION_RECORDS = new ConcurrentHashMap<>();

    /**
     * 检查执行次数限制条件
     * 验证规则在特定时间窗口内的执行次数是否超过限制
     *
     * @param executionCondition 执行条件对象，可能为null
     * @return 如果执行次数限制条件满足返回true（即执行未超限），否则返回false
     */
    @Override
    public boolean check(ThingsRules.ExecutionCondition executionCondition) {
        // 如果没有执行条件，则认为条件满足
        if (executionCondition == null) {
            return true;
        }

        // 获取执行次数限制条件
        ThingsRules.ExecutionLimit executionLimit = executionCondition.getExecutionLimit();
        if (executionLimit != null) {
            // 检查执行次数是否超限
            return isExecutionLimitNotExceeded(executionLimit);
        }

        // 如果没有执行次数限制条件，则认为条件满足
        return true;
    }

    /**
     * 获取条件类型
     * 返回该条件检查器处理的条件类型标识
     *
     * @return 条件类型字符串
     */
    @Override
    public String getType() {
        return "execution_limit";
    }

    /**
     * 检查执行次数是否未超限
     * 根据执行限制条件检查当前执行次数是否超过限制
     *
     * @param executionLimit 执行次数限制对象
     * @return 如果执行次数未超限返回true，否则返回false
     */
    private boolean isExecutionLimitNotExceeded(ThingsRules.ExecutionLimit executionLimit) {
        try {
            int maxExecutions = executionLimit.getCount();
            int timeWindow = parsePeriodToSeconds(executionLimit.getPeriod());
            String ruleId = String.valueOf(executionLimit.hashCode());
            ExecutionRecord record = getOrCreateExecutionRecord(ruleId);
            return checkAndRecordExecution(record, maxExecutions, timeWindow, ruleId);
        } catch (Exception e) {
            log.warn("Failed to check execution limit condition: {}", executionLimit, e);
            return true;
        }
    }

    private ExecutionRecord getOrCreateExecutionRecord(String ruleId) {
        return EXECUTION_RECORDS.computeIfAbsent(ruleId, k -> new ExecutionRecord());
    }

    private boolean checkAndRecordExecution(ExecutionRecord record, int maxExecutions, int timeWindow, String ruleId) {
        LocalDateTime now = LocalDateTime.now();
        cleanExpiredExecutions(record, now, timeWindow);
        if (record.executions.size() >= maxExecutions) {
            log.info("Execution limit exceeded for rule: {}, max: {}, current: {}",
                    ruleId, maxExecutions, record.executions.size());
            return false;
        }
        record.executions.add(now);
        return true;
    }

    private void cleanExpiredExecutions(ExecutionRecord record, LocalDateTime now, int timeWindow) {
        record.executions.removeIf(executionTime ->
                executionTime.isBefore(now.minusSeconds(timeWindow)));
    }

    /**
     * 将时间周期字符串解析为秒数
     * 支持格式如: 1H(1小时), 30M(30分钟), 5S(5秒)等
     * 如果解析失败则返回默认值1小时(3600秒)
     *
     * @param period 时间周期字符串
     * @return 解析得到的秒数
     */
    private int parsePeriodToSeconds(String period) {
        // 如果参数为空，返回默认值1小时
        if (period == null || period.isEmpty()) {
            return 3600; // 默认1小时
        }

        try {
            // 解析格式：提取数值和单位
            // 例如从"1H"得到数值1和单位H
            char unit = period.charAt(period.length() - 1);
            int value = Integer.parseInt(period.substring(0, period.length() - 1));

            // 根据不同单位转换为秒
            switch (Character.toUpperCase(unit)) {
                case 'H': // 小时
                    return value * 3600;
                case 'M': // 分钟
                    return value * 60;
                case 'S': // 秒
                    return value;
                default:
                    // 对于未知单位，假设为秒
                    return value;
            }
        } catch (Exception e) {
            // 记录警告日志并返回默认值
            log.warn("Failed to parse period: {}, using default 1 hour", period);
            return 3600; // 默认1小时
        }
    }

    /**
     * 执行记录类
     * 用于跟踪规则的执行历史
     * <p>
     * 特性：
     * - 使用线程安全的列表存储执行时间
     * - 提供清理过期记录的方法
     */
    private static class ExecutionRecord {
        // 存储执行时间的线程安全列表
        // 使用CopyOnWriteArrayList以优化读多写少的场景
        final java.util.List<LocalDateTime> executions = new java.util.concurrent.CopyOnWriteArrayList<>();
    }
}