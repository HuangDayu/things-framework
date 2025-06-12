package cn.huangdayu.things.sofaark.manager;

import cn.huangdayu.things.api.infrastructure.ThingsConfigurator;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.properties.ThingsEngineProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsSofaArkManager {

    private final ThingsConfigurator thingsConfigurator;

    private ThingsSofaArkJarMonitor thingsSofaArkJarMonitor;

    @PostConstruct
    private void init() {
        ThingsEngineProperties.ThingsSofaArkProperties sofaArk = thingsConfigurator.getProperties().getSofaArk();
        if (sofaArk.isEnableArkAutoManage()) {
            thingsSofaArkJarMonitor = new ThingsSofaArkJarMonitor(sofaArk.getArkBizDirectory(), sofaArk.getArkBizJarEndsWith(), "-unpack", new ThingsSofaArkJarListener());
            thingsSofaArkJarMonitor.start();
        }
    }

    @PreDestroy
    private void destroy() {
        if (thingsSofaArkJarMonitor != null) {
            thingsSofaArkJarMonitor.stop();
        }
    }


}
