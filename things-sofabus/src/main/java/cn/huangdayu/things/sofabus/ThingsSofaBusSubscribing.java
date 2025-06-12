package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.api.container.ThingsDescriber;
import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.message.ThingsSubscriber;
import cn.huangdayu.things.api.sofabus.ThingsSofaBusSubscriber;
import cn.huangdayu.things.common.wrapper.ThingsSubscribes;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static cn.huangdayu.things.common.utils.ThingsUtils.createDslSubscribes;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
public class ThingsSofaBusSubscribing implements ThingsSofaBusSubscriber {

    private final ThingsChaining thingsChaining;
    private final ThingsDescriber thingsDescriber;

    @Override
    public ThingsSubscriber create(ThingsSubscribes thingsSubscribes) {
        return thingsChaining::input;
    }

    @Override
    public Set<ThingsSubscribes> getDslSubscribes() {
        return createDslSubscribes(this, thingsDescriber.getDSL());
    }


}
