package cn.huangdayu.things.session;

import cn.huangdayu.things.api.infrastructure.ThingsCacheService;
import cn.huangdayu.things.api.infrastructure.ThingsSessionService;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.observer.event.ThingsSessionUpdatedEvent;
import cn.huangdayu.things.common.wrapper.ThingsSession;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsSessionExecutor implements ThingsSessionService {

    private final ThingsCacheService thingsCacheService;
    private final ThingsEventObserver thingsEventObserver;


    @PostConstruct
    public void init() {
        thingsEventObserver.registerObserver(ThingsSessionUpdatedEvent.class, event -> {
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
        return thingsCacheService.get(productCode + ThingsConstants.THINGS_SEPARATOR + deviceCode, ThingsSession.class);
    }

    @Override
    public void addSession(ThingsSession session) {
        thingsCacheService.put(session.getProductCode() + ThingsConstants.THINGS_SEPARATOR + session.getDeviceCode(), session);
    }

    @Override
    public void removeSession(ThingsSession session) {
        thingsCacheService.remove(session.getProductCode() + ThingsConstants.THINGS_SEPARATOR + session.getDeviceCode());
    }

    @Override
    public void removeSession(String productCode, String deviceCode) {
        thingsCacheService.remove(productCode + ThingsConstants.THINGS_SEPARATOR + deviceCode);

    }
}
