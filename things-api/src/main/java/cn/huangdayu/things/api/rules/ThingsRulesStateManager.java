package cn.huangdayu.things.api.rules;

/**
 * 状态管理
 *
 * @author huangdayu
 */
public interface ThingsRulesStateManager extends ThingsRulesHandler {

    /**
     * 清空状态
     *
     * @param sessionId
     */
    void clearState(String sessionId);

    /**
     * 保存状态
     *
     * @param sessionId
     * @param state
     */
    void saveStringState(String sessionId, String state);

    /**
     * 加载状态
     *
     * @param sessionId
     * @return
     */
    String loadStringState(String sessionId);
}
