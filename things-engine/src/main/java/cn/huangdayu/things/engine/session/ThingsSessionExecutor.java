package cn.huangdayu.things.engine.session;

import cn.huangdayu.things.engine.annotation.ThingsBean;
import cn.huangdayu.things.engine.async.ThingsSessionStatusEvent;
import cn.huangdayu.things.engine.core.ThingsObserverEngine;
import cn.huangdayu.things.engine.core.ThingsSessionEngine;
import cn.huangdayu.things.engine.infrastructure.CacheService;
import cn.huangdayu.things.engine.wrapper.ThingsSession;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.engine.common.ThingsConstants.THINGS_SEPARATOR;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsSessionExecutor implements ThingsSessionEngine {

    private final CacheService cacheService;
    private final ThingsObserverEngine thingsObserverEngine;


    @PostConstruct
    public void init() {
        thingsObserverEngine.registerObserver(ThingsSessionStatusEvent.class, event -> {
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
        return cacheService.get(productCode + THINGS_SEPARATOR + deviceCode, ThingsSession.class);
    }

    @Override
    public void addSession(ThingsSession session) {
        cacheService.put(session.getProductCode() + THINGS_SEPARATOR + session.getDeviceCode(), session);
    }

    @Override
    public void removeSession(ThingsSession session) {
        cacheService.remove(session.getProductCode() + THINGS_SEPARATOR + session.getDeviceCode());
    }

    @Override
    public void removeSession(String productCode, String deviceCode) {
        cacheService.remove(productCode + THINGS_SEPARATOR + deviceCode);

    }
}
