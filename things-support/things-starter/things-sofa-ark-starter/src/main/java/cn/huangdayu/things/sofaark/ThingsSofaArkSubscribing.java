package cn.huangdayu.things.sofaark;

import cn.huangdayu.things.api.container.ThingsDescriber;
import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.message.ThingsSubscriber;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusDescriber;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusSubscriber;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.ThingsDslInfo;
import cn.huangdayu.things.common.wrapper.ThingsSubscribes;
import com.alipay.sofa.ark.api.ArkClient;
import com.alipay.sofa.ark.container.model.BizModel;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.model.BizState;
import com.alipay.sofa.koupleless.common.BizRuntimeContextRegistry;
import com.alipay.sofa.koupleless.common.api.SpringServiceFinder;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static cn.huangdayu.things.common.utils.ThingsUtils.createDslSubscribes;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class ThingsSofaArkSubscribing implements ThingsSofaBusSubscriber {

    private final Map<BizModel, ThingsSubscriber> thingsSubscriberMap = new ConcurrentHashMap<>();

    public static <T> T getModuleService(String bizName, String bizVersion, Class<T> serviceType) {
        try {
            return SpringServiceFinder.getModuleService(bizName, bizVersion, serviceType);
        } catch (Exception e) {
            log.warn("Things SofaArk get module [{}:{}] service [{}] error: {}", bizName, bizVersion, serviceType.getName(), e.getMessage());
        }
        return null;
    }

    @PreDestroy
    private void stop() {
        thingsSubscriberMap.clear();
    }

    @Override
    public ThingsSubscriber create(ThingsSubscribes thingsSubscribes) {
        if (thingsSubscribes.getSubscriber() instanceof BizModel bizModel) {
            ThingsSubscriber thingsSubscriber = thingsSubscriberMap.computeIfAbsent(bizModel, s -> {
                ThingsChaining thingsChaining = getModuleService(bizModel.getBizName(), bizModel.getBizVersion(), ThingsChaining.class);
                return thingsChaining != null ? thingsChaining::input : null;
            });
            if (thingsSubscriber != null) {
                return thingsSubscriber;
            }
        }
        ApplicationContext applicationContext = (ApplicationContext) BizRuntimeContextRegistry.getMasterBizRuntimeContext().getApplicationContext().get();
        ThingsChaining thingsChaining = applicationContext.getBean(ThingsChaining.class);
        return thingsChaining::input;
    }


    @Override
    public Set<ThingsSubscribes> getDslSubscribes() {
        Set<ThingsSubscribes> subscribes = new CopyOnWriteArraySet<>();
        Biz masterBiz = ArkClient.getMasterBiz();
        ArkClient.getBizManagerService().getBizInOrder().forEach(biz -> {
            if (biz != null && biz.getBizState().equals(BizState.ACTIVATED) && !biz.equals(masterBiz)) {
                try {
                    ThingsSofaBusDescriber thingsSofaBusDescriber = getModuleService(biz.getBizName(), biz.getBizVersion(), ThingsSofaBusDescriber.class);
                    if (thingsSofaBusDescriber != null) {
                        ThingsDslInfo dslInfo = thingsSofaBusDescriber.getDSL();
                        Set<ThingsSubscribes> dslSubscribes = createDslSubscribes(biz, dslInfo);
                        subscribes.addAll(dslSubscribes);
                        log.info("Things SofaArk get DSL [{}] for [{}:{}] success", dslSubscribes.size(), biz.getBizName(), biz.getBizVersion());
                    }
                } catch (Exception e) {
                    log.info("Things SofaArk get DSL for [{}:{}] service [{}] error: {}", biz.getBizName(), biz.getBizVersion(), ThingsDescriber.class.getName(), e.getMessage());
                }
            }
        });
        return subscribes;
    }

}
