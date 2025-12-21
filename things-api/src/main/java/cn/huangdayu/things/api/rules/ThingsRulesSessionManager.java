package cn.huangdayu.things.api.rules;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsRulesSessionManager {

    /**
     * 获取指定路由下的所有会话
     *
     * @param routeId
     * @return
     */
    Set<String> getSession(String routeId);

    /**
     * 添加会话
     *
     * @param routeId
     * @param sessionId
     */
    void addSession(String routeId, String sessionId);

    /**
     * 移除会话
     *
     * @param routeId
     * @param sessionId
     */
    void removeSession(String routeId, String sessionId);

    /**
     * 清空指定路由下的所有会话
     *
     * @param routeId
     */
    void clearSession(String routeId);


}
