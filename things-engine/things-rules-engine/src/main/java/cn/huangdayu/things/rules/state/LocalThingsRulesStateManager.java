package cn.huangdayu.things.rules.state;

import cn.huangdayu.things.api.rules.ThingsRulesStateManager;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.rules.ThingsRules;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地规则状态管理器
 * 用于支持跨时间条件的规则执行
 *
 * @author huangdayu
 */
@ThingsBean
public class LocalThingsRulesStateManager implements ThingsRulesStateManager {

    private final ConcurrentHashMap<String, Object> stateMap = new ConcurrentHashMap<>();

    @Override
    public void saveState(String sessionId, ThingsRules thingsRules) {
        stateMap.put(sessionId, thingsRules);
    }

    @Override
    public ThingsRules loadState(String sessionId) {
        Object state = stateMap.get(sessionId);
        if (state instanceof ThingsRules) {
            return (ThingsRules) state;
        }
        return null;
    }

    @Override
    public void clearState(String sessionId) {
        stateMap.remove(sessionId);
    }

    @Override
    public String getType() {
        return "local_state";
    }

    /**
     * 保存字符串状态（用于跨时间条件）
     */
    public void saveStringState(String sessionId, String state) {
        stateMap.put(sessionId, state);
    }

    /**
     * 加载字符串状态（用于跨时间条件）
     */
    public String loadStringState(String sessionId) {
        Object state = stateMap.get(sessionId);
        if (state instanceof String) {
            return (String) state;
        }
        return null;
    }
}