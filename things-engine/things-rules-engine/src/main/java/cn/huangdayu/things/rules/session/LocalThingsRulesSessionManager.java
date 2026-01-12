package cn.huangdayu.things.rules.session;

import cn.huangdayu.things.api.rules.ThingsRulesSessionManager;
import cn.huangdayu.things.common.annotation.ThingsBean;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class LocalThingsRulesSessionManager implements ThingsRulesSessionManager {
    @Override
    public Set<String> getSession(String routeId) {
        return Set.of();
    }

    @Override
    public void addSession(String routeId, String sessionId) {

    }

    @Override
    public void removeSession(String routeId, String sessionId) {

    }

    @Override
    public void clearSession(String routeId) {

    }

    @Override
    public String getType() {
        return "local_session";
    }
}
