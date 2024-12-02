package cn.huangdayu.things.api.instances;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsInstancesProvider {


    /**
     * 提供的物模型的productCode
     */
    Set<String> getProvides();


    /**
     * 消费的物模型的productCode
     * 消费的服务，订阅的事件
     */
    Set<String> getConsumes();


    /**
     * 订阅列表
     */
    Set<String> getSubscribes();

}
