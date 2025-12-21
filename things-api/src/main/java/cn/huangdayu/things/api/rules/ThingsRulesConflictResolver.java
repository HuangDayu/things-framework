package cn.huangdayu.things.api.rules;

import cn.huangdayu.things.common.dsl.rules.ThingsRules;

import java.util.Set;

/**
 * 解决规则冲突
 *
 * @author huangdayu
 */
public interface ThingsRulesConflictResolver extends ThingsRulesHandler {

    /**
     * 冲突解决
     *
     * @param triggeredRules
     * @return
     */
    Set<ThingsRules> resolve(Set<ThingsRules> triggeredRules);


}
