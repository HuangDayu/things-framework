package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.api.container.ThingsDescriber;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusDescriber;
import cn.huangdayu.things.common.dsl.ThingsDslInfo;
import lombok.RequiredArgsConstructor;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
public class ThingsSofaBusDescribing implements ThingsSofaBusDescriber {

    private final ThingsDescriber thingsDescriber;

    @Override
    public ThingsDslInfo getDSL() {
        return thingsDescriber.getDSL();
    }
}
