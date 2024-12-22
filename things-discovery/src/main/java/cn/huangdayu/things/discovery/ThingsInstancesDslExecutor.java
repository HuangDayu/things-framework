package cn.huangdayu.things.discovery;

import cn.huangdayu.things.api.infrastructure.ThingsConfigService;
import cn.huangdayu.things.api.instances.ThingsInstancesDslManager;
import cn.huangdayu.things.api.instances.ThingsInstancesProvider;
import cn.huangdayu.things.api.instances.ThingsInstancesSubscriber;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.DomainInfo;
import cn.huangdayu.things.common.dsl.DomainSubscribeInfo;
import cn.huangdayu.things.common.dsl.DslInfo;
import cn.huangdayu.things.common.dsl.ThingsInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
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
public class ThingsInstancesDslExecutor implements ThingsInstancesDslManager, ThingsInstancesSubscriber, ThingsInstancesProvider {

    public static final Set<DslInfo> DSL_INFO_SET = new ConcurrentHashSet<>();
    public static final Set<ThingsInfo> THINGS_INFO_SET = new ConcurrentHashSet<>();
    public static final Set<DomainInfo> DOMAIN_INFO_SET = new ConcurrentHashSet<>();
    private final ThingsConfigService thingsConfigService;

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
        return new DslInfo(thingsConfigService.getProperties().getInstance(), DOMAIN_INFO_SET, THINGS_INFO_SET);
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

    @Override
    public Set<ThingsInstance> getProvides(JsonThingsMessage jtm) {
        if (CollUtil.isNotEmpty(DSL_INFO_SET)) {
            return DSL_INFO_SET.parallelStream().filter(dslInfo -> isProvided(dslInfo.getThingsDsl(), jtm)).map(DslInfo::getInstance).collect(Collectors.toSet());
        }
        return Set.of();
    }

    private boolean isProvided(Set<ThingsInfo> thingsDsl, JsonThingsMessage jtm) {
        return thingsDsl.parallelStream().anyMatch(thingsInfo -> {
            String code = thingsInfo.getProfile().getProduct().getCode();
            return jtm.getBaseMetadata().getProductCode().equals(code);
        });
    }
}
