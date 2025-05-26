package cn.huangdayu.things.rest.instances;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsInstance;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsInstancesSubscriber {

    Set<ThingsInstance> getSubscribes(JsonThingsMessage jtm);

}
