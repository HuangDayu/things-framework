package cn.huangdayu.things.api.rules;

import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import cn.huangdayu.things.common.message.ThingsResponseMessage;

/**
 * 引擎执行器
 *
 * @author huangdayu
 */
public interface ThingsRulesEngineExecutor {

    /**
     * 执行
     *
     * @param trm
     * @return
     */
    ThingsResponseMessage execute(ThingsRequestMessage trm);


    /**
     * 执行规则
     *
     * @param thingsRules
     * @param message
     * @return
     */
    ThingsResponseMessage executeRule(ThingsRules thingsRules, ThingsRequestMessage message);
}
