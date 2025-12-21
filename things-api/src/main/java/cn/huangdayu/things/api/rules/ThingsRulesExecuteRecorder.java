package cn.huangdayu.things.api.rules;

import cn.huangdayu.things.common.dsl.rules.ThingsRules;

import java.util.List;

/**
 * 规则执行记录器
 *
 * @author huangdayu
 */
public interface ThingsRulesExecuteRecorder extends ThingsRulesHandler {

    /**
     * 记录规则执行
     *
     * @param thingsRules
     * @param handlers
     */
    void record(ThingsRules thingsRules, List<ThingsRulesHandler> handlers);

}
