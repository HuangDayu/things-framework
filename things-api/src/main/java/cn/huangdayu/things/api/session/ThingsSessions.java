package cn.huangdayu.things.api.session;

import cn.huangdayu.things.common.wrapper.ThingsSession;

/**
 * @author huangdayu
 */
public interface ThingsSessions {

    ThingsSession getSession(String productCode, String deviceCode);


    void addSession(ThingsSession session);


    void removeSession(ThingsSession session);


    void removeSession(String productCode, String deviceCode);

}
