package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.engine.wrapper.ThingsSession;

/**
 * @author huangdayu
 */
public interface ThingsSessionEngine {

    ThingsSession getSession(String productCode, String deviceCode);


    void addSession(ThingsSession session);


    void removeSession(ThingsSession session);


    void removeSession(String productCode, String deviceCode);

}
