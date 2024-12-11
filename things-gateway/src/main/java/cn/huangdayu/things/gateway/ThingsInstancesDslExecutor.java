package cn.huangdayu.things.gateway;

import cn.huangdayu.things.api.instances.ThingsInstancesDslManager;
import cn.huangdayu.things.api.instances.ThingsInstancesSubscriber;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.DomainInfo;
import cn.huangdayu.things.common.dsl.DomainSubscribeInfo;
import cn.huangdayu.things.common.dsl.DslInfo;
import cn.huangdayu.things.common.dsl.ThingsInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_WILDCARD;
import static cn.huangdayu.things.common.utils.ThingsUtils.subIdentifies;

/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsInstancesDslExecutor implements ThingsInstancesDslManager, ThingsInstancesSubscriber {

    public static final Set<DslInfo> DSL_INFO_SET = new ConcurrentHashSet<>();
    public static final Set<ThingsInfo> THINGS_INFO_SET = new ConcurrentHashSet<>();
    public static final Set<DomainInfo> DOMAIN_INFO_SET = new ConcurrentHashSet<>();
    private final ThingsFrameworkProperties thingsFrameworkProperties;

    @Override
    public void addAllDsl(DslInfo dslInfo) {
        if (dslInfo != null) {
            if (CollUtil.isNotEmpty(dslInfo.getThingsDsl())) {
                THINGS_INFO_SET.addAll(dslInfo.getThingsDsl());
            }
            if (CollUtil.isNotEmpty(dslInfo.getDomainDsl())) {
                DOMAIN_INFO_SET.addAll(dslInfo.getDomainDsl());
            }
            DSL_INFO_SET.add(dslInfo);
        }
    }

    @Override
    public DslInfo getDsl() {
        return new DslInfo(thingsFrameworkProperties.getInstance(), DOMAIN_INFO_SET, THINGS_INFO_SET);
    }

    @Override
    public Set<ThingsInstance> getSubscribes(JsonThingsMessage jtm) {
        return getSubscribeInstances(jtm.getBaseMetadata().getProductCode(), jtm.getMethod());
    }

    private Set<ThingsInstance> getSubscribeInstances(String productCode, String method) {
        return DSL_INFO_SET.parallelStream().filter(dslInfo -> isSubscribed(dslInfo.getDomainDsl(), productCode, method)).map(DslInfo::getInstance).collect(Collectors.toSet());
    }

    private boolean isSubscribed(Set<DomainInfo> domainDsl, String productCode, String method) {
        return domainDsl.parallelStream().anyMatch(instance -> {
            Set<DomainSubscribeInfo> subscribes = instance.getSubscribes();
            if (StrUtil.isNotBlank(method)) {
                String identifier = subIdentifies(method);
                return subscribes.contains(new DomainSubscribeInfo(THINGS_WILDCARD, identifier)) || subscribes.contains(new DomainSubscribeInfo(productCode, identifier));
            }
            return subscribes.contains(new DomainSubscribeInfo(productCode, THINGS_WILDCARD));
        });
    }
}
