package cn.huangdayu.things.gateway;

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

import java.util.Set;
import java.util.stream.Collectors;

import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_WILDCARD;
import static cn.huangdayu.things.common.utils.ThingsUtils.subIdentifies;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsDslExecutor implements ThingsDslManager, ThingsInstancesSubscriber {

    public static final Set<ThingsInfo> THINGS_INFO_SET = new ConcurrentHashSet<>();
    public static final Set<DomainInfo> DOMAIN_INFO_SET = new ConcurrentHashSet<>();

    @Override
    public void addDsl(DslInfo dslInfo) {
        if (dslInfo != null) {
            if (CollUtil.isNotEmpty(dslInfo.getThingsDsl())) {
                THINGS_INFO_SET.addAll(dslInfo.getThingsDsl());
            }
            if (CollUtil.isNotEmpty(dslInfo.getDomainDsl())) {
                DOMAIN_INFO_SET.addAll(dslInfo.getDomainDsl());
            }
        }
    }

    @Override
    public DslInfo getDsl() {
        return new DslInfo(DOMAIN_INFO_SET, THINGS_INFO_SET);
    }

    @Override
    public Set<ThingsInstance> getSubscribes(JsonThingsMessage jtm) {
        return getSubscribeInstances(jtm.getBaseMetadata().getProductCode(), jtm.getMethod());
    }

    private Set<ThingsInstance> getSubscribeInstances(String productCode, String identifier) {
        return DOMAIN_INFO_SET.parallelStream().filter(instance -> {
            if (StrUtil.isNotBlank(identifier)) {
                String identifier1 = subIdentifies(identifier);
                return instance.getSubscribes().contains(new DomainSubscribeInfo(THINGS_WILDCARD, identifier1)) || instance.getSubscribes().contains(new DomainSubscribeInfo(productCode, identifier1));
            }
            return instance.getSubscribes().contains(new DomainSubscribeInfo(productCode, THINGS_WILDCARD));
        }).map(domainInfo -> domainInfo.getProfile().getInstance()).collect(Collectors.toSet());
    }
}
