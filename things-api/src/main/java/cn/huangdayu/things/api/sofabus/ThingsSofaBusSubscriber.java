package cn.huangdayu.things.api.sofabus;

import cn.huangdayu.things.common.wrapper.ThingsSubscribes;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsSofaBusSubscriber {

    /**
     * 获取订阅信息列表
     * @return
     */
    Set<ThingsSubscribes> getSubscribes();

}
