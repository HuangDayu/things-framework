package cn.huangdayu.things.sofabiz.multiple;

import cn.huangdayu.things.api.container.ThingsContainer;
import cn.huangdayu.things.api.container.ThingsDescriber;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusDescriber;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.ThingsDslInfo;
import cn.huangdayu.things.sofabiz.condition.ThingsSofaBizMultipleCondition;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Conditional;

import java.util.Map;

/**
 * @author huangdayu
 */
@Conditional(ThingsSofaBizMultipleCondition.class)
@ThingsBean
@RequiredArgsConstructor
public class ThingsSofaBizDescriber implements ThingsSofaBusDescriber {

    private final ApplicationContext applicationContext;
    private final ThingsDescriber thingsDescriber;

    @Override
    public ThingsDslInfo getDSL() {
        ThingsDslInfo thingsDslInfo = new ThingsDslInfo();
        Map<String, ThingsContainer> beansOfType = applicationContext.getBeansOfType(ThingsContainer.class);
        beansOfType.forEach((s, thingsContainer) -> {
            ThingsDslInfo dsl = thingsDescriber.getDSL(thingsContainer);
            if (CollUtil.isNotEmpty(dsl.getDomainDsl())) {
                thingsDslInfo.getDomainDsl().addAll(dsl.getDomainDsl());
            }
            if (CollUtil.isNotEmpty(dsl.getThingsDsl())) {
                thingsDslInfo.getThingsDsl().addAll(dsl.getThingsDsl());
            }
        });
        return thingsDslInfo;
    }
}
