package cn.huangdayu.things.sofaark;

import cn.huangdayu.things.api.container.ThingsDescriber;
import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.message.ThingsSubscriber;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusDescriber;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusSubscriber;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.ThingsDslInfo;
import cn.huangdayu.things.common.wrapper.ThingsSubscribes;
import cn.hutool.core.util.ReflectUtil;
import com.alipay.sofa.ark.api.ArkClient;
import com.alipay.sofa.ark.container.model.BizModel;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.model.BizState;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static cn.huangdayu.things.common.utils.ThingsUtils.createDslSubscribes;
import static cn.huangdayu.things.sofaark.ThingsSofaArkUtils.getArkService;
import static cn.huangdayu.things.sofaark.ThingsSofaArkUtils.getBizService;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsBean
public class ThingsSofaArkSubscribing implements ThingsSofaBusSubscriber {

    private final Map<Object, ThingsSubscriber> thingsSubscriberMap = new ConcurrentHashMap<>();


    @PreDestroy
    private void stop() {
        thingsSubscriberMap.clear();
    }

    @Override
    public ThingsSubscriber create(ThingsSubscribes thingsSubscribes) {
        if (thingsSubscribes.getSubscriber() != null && thingsSubscribes.getSubscriber().getClass().getName().equals(BizModel.class.getName())) {
            // 由于BizModel对象和class由不同的classloader加载，只能使用反射的获取字段
            Object bizName = ReflectUtil.getFieldValue(thingsSubscribes.getSubscriber(), "bizName");
            Object bizVersion = ReflectUtil.getFieldValue(thingsSubscribes.getSubscriber(), "bizVersion");
            return thingsSubscriberMap.computeIfAbsent(thingsSubscribes.getSubscriber(),
                    s -> getBizService(String.valueOf(bizName), String.valueOf(bizVersion), ThingsSubscriber.class));
        }
        return new ThingsSofaArkSubscriber(getArkService(ThingsChaining.class));
    }


    @Override
    public Set<ThingsSubscribes> getDslSubscribes() {
        Set<ThingsSubscribes> subscribes = new CopyOnWriteArraySet<>();
        Biz masterBiz = ArkClient.getMasterBiz();
        ArkClient.getBizManagerService().getBizInOrder().forEach(biz -> {
            if (biz != null && biz.getBizState().equals(BizState.ACTIVATED) && !biz.equals(masterBiz)) {
                try {
                    ThingsSofaBusDescriber thingsSofaBusDescriber = getBizService(biz.getBizName(), biz.getBizVersion(), ThingsSofaBusDescriber.class);
                    if (thingsSofaBusDescriber != null) {
                        ThingsDslInfo dslInfo = thingsSofaBusDescriber.getDSL();
                        subscribes.addAll(createDslSubscribes(biz, dslInfo));
                    }
                } catch (Exception e) {
                    log.info("Things SofaArk get DSL for [{}:{}] service [{}] error: {}", biz.getBizName(), biz.getBizVersion(), ThingsDescriber.class.getName(), e.getMessage());
                }
            }
        });
        return subscribes;
    }

}
