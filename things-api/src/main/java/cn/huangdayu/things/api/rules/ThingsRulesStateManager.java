package cn.huangdayu.things.api.rules;

import cn.huangdayu.things.common.dsl.rules.ThingsRules;

/**
 * 状态管理
 *
 * @author huangdayu
 */
public interface ThingsRulesStateManager extends ThingsRulesHandler {

    /**
     * 保存状态
     *
     * @param sessionId
     * @param thingsRules
     */
    void saveState(String sessionId, ThingsRules thingsRules);

    /**
     * 加载状态
     *
     * @param sessionId
     * @return
     */
    ThingsRules loadState(String sessionId);

    /**
     * 清空状态
     *
     * @param sessionId
     */
    void clearState(String sessionId);

}
