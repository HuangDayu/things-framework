package cn.huangdayu.things.session;

import cn.huangdayu.things.api.infrastructure.CacheService;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.event.ThingsEventObserver;
import cn.huangdayu.things.common.event.ThingsSessionStatusEvent;
import cn.huangdayu.things.common.wrapper.ThingsSession;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsSessionExecutor implements ThingsSessionManager {

    private final CacheService cacheService;
    private final ThingsEventObserver thingsEventObserver;


    @PostConstruct
    public void init() {
        thingsEventObserver.registerObserver(ThingsSessionStatusEvent.class, event -> {
            ThingsSession session = event.getSession();
            if (session != null) {
                if (session.isOnline()) {
                    addSession(session);
                } else {
                    removeSession(session);
                }
            }
        });
    }

    @Override
    public ThingsSession getSession(String productCode, String deviceCode) {
        return cacheService.get(productCode + ThingsConstants.THINGS_SEPARATOR + deviceCode, ThingsSession.class);
    }

    @Override
    public void addSession(ThingsSession session) {
        cacheService.put(session.getProductCode() + ThingsConstants.THINGS_SEPARATOR + session.getDeviceCode(), session);
    }

    @Override
    public void removeSession(ThingsSession session) {
        cacheService.remove(session.getProductCode() + ThingsConstants.THINGS_SEPARATOR + session.getDeviceCode());
    }

    @Override
    public void removeSession(String productCode, String deviceCode) {
        cacheService.remove(productCode + ThingsConstants.THINGS_SEPARATOR + deviceCode);

    }
}
