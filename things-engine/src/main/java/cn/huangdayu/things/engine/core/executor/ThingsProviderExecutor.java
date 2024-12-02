package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.api.instances.ThingsInstancesProvider;
import cn.huangdayu.things.common.annotation.ThingsBean;

import java.util.Set;
import java.util.stream.Collectors;

import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_SEPARATOR;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsProviderExecutor implements ThingsInstancesProvider {
    @Override
    public Set<String> getProvides() {
        return ThingsBaseExecutor.THINGS_SERVICES_TABLE.columnKeySet();
    }

    @Override
    public Set<String> getConsumes() {
        return ThingsBaseExecutor.THINGS_EVENTS_LISTENER_TABLE.columnKeySet();
    }

    @Override
    public Set<String> getSubscribes() {
        return ThingsBaseExecutor.THINGS_EVENTS_LISTENER_TABLE.cellSet()
                .stream().map(cell -> cell.getColumnKey() + THINGS_SEPARATOR + cell.getRowKey()).collect(Collectors.toSet());
    }
}
