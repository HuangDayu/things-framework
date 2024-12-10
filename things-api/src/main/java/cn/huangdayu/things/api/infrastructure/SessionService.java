package cn.huangdayu.things.api.infrastructure;

import cn.huangdayu.things.common.wrapper.ThingsSession;

/**
 * @author huangdayu
 */
public interface SessionService {

    ThingsSession getSession(String productCode, String deviceCode);


    void addSession(ThingsSession session);


    void removeSession(ThingsSession session);


    void removeSession(String productCode, String deviceCode);

}
