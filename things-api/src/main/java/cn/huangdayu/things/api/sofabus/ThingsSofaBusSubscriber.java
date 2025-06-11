package cn.huangdayu.things.api.sofabus;

import cn.huangdayu.things.api.message.ThingsSubscriber;
import cn.huangdayu.things.common.wrapper.ThingsSubscribes;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsSofaBusSubscriber {


    ThingsSubscriber create(ThingsSubscribes thingsSubscribes);


    Set<ThingsSubscribes> getDslSubscribes();

}
