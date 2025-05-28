package cn.huangdayu.things.camel;

import cn.huangdayu.things.camel.converter.AbstractTypeConverter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.ServiceStatus;
import org.apache.camel.impl.event.RouteStoppedEvent;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.spi.RouteController;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ThingsCamelConfiguration {

    private final CamelContext camelContext;
    private final Map<String, AbstractTypeConverter> typeConverterMap;

    @PostConstruct
    public void init() {
        if (typeConverterMap != null) {
            for (Map.Entry<String, AbstractTypeConverter> entry : typeConverterMap.entrySet()) {
                AbstractTypeConverter converter = entry.getValue();
                camelContext.getTypeConverterRegistry().addTypeConverter(converter.toType(), converter.fromType(), converter.typeConverter());
            }
        }
        camelContext.getManagementStrategy().addEventNotifier(new EventNotifierSupport() {
            @Override
            public void notify(CamelEvent event) throws Exception {
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
                return event instanceof RouteStoppedEvent;
            }
        });
    }

}
