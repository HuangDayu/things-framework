package cn.huangdayu.things.rules.state;

import cn.huangdayu.things.api.rules.ThingsRulesStateManager;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class LocalThingsRulesStateManager implements ThingsRulesStateManager {
    @Override
    public void saveState(String sessionId, ThingsRules thingsRules) {

    }

    @Override
    public ThingsRules loadState(String sessionId) {
        return null;
    }

    @Override
    public void clearState(String sessionId) {

    }

    @Override
    public String getType() {
        return "local_state";
    }
}
