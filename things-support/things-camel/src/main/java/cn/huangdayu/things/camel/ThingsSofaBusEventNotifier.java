package cn.huangdayu.things.camel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.ServiceStatus;
import org.apache.camel.impl.event.AbstractRouteEvent;
import org.apache.camel.impl.event.RouteStoppedEvent;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.spi.RouteController;
import org.apache.camel.support.EventNotifierSupport;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@Slf4j
public class ThingsSofaBusEventNotifier extends EventNotifierSupport {


    private final CamelContext camelContext;

    @Override
    public void notify(CamelEvent event) throws Exception {
        log.debug("Things camel context event: {}", event.getType());
        if (event instanceof RouteStoppedEvent e) {
            try {
                // 自动重启路由（需处理线程安全问题）
                RouteController routeController = camelContext.getRouteController();
                ServiceStatus routeStatus = routeController.getRouteStatus(e.getRoute().getId());
                if (!ServiceStatus.Starting.equals(routeStatus) && !ServiceStatus.Started.equals(routeStatus)) {
                    routeController.startRoute(e.getRoute().getId());
                }
            } catch (Exception ex) {
                log.warn("Route [{}] restart error", e.getRoute().getId(), ex);
            }
        }
    }

    @Override
    public boolean isEnabled(CamelEvent event) {
        return event instanceof AbstractRouteEvent;
    }

}
