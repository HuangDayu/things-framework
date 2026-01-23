package cn.huangdayu.things.api.rules;

import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.common.message.ThingsRequestMessage;

/**
 * 触发器处理器接口
 * 定义统一的触发器处理标准
 * 支持跨时间和非跨时间两种处理方式
 *
 * @author huangdayu
 */
public interface ThingsRulesTriggerProcessor extends ThingsRulesHandler {


    /**
     * 判断处理器是否可以处理该规则
     *
     * @param thingsRules
     * @return
     */
    boolean canProcess(ThingsRules thingsRules);

    /**
     * 匹配触发器
     *
     * @param thingsRules 规则对象
     * @param message     请求消息
     * @return 是否匹配成功
     */
    boolean matchTriggers(ThingsRules thingsRules, ThingsRequestMessage message);
}