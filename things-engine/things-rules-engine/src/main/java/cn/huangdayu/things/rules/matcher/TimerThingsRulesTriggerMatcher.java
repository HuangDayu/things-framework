package cn.huangdayu.things.rules.matcher;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.api.rules.ThingsRulesTriggerMatcher;
import lombok.extern.slf4j.Slf4j;

/**
 * 定时器触发器匹配器
 * 负责匹配定时器类型的触发器
 * 处理定时任务与规则定义的触发条件之间的匹配逻辑
 *
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class TimerThingsRulesTriggerMatcher implements ThingsRulesTriggerMatcher {

    /**
     * 匹配定时器触发器条件
     * 对于定时器触发器，任何消息都可以触发，因为定时器是系统内部触发的
     *
     * @param condition 触发条件对象
     * @param message   触发规则执行的消息
     * @return 如果匹配成功返回true，否则返回false
     */
    @Override
    public boolean match(ThingsRules.TriggerCondition condition, ThingsRequestMessage message) {
        // 检查是否包含Cron表达式
        if (condition.getCron() != null) {
            // 记录定时器触发日志
            logTimerTrigger(condition.getCron());
            return true;
        }
        return false;
    }

    /**
     * 获取触发器类型
     *
     * @return 触发器类型字符串
     */
    @Override
    public String getType() {
        return "timer";
    }

    /**
     * 记录定时器触发日志
     *
     * @param cron Cron表达式
     */
    private void logTimerTrigger(String cron) {
        log.info("Matching timer trigger with cron: {}", cron);
    }
}