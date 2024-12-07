package cn.huangdayu.things.api.instances;

import java.util.HashSet;
import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsInstancesProvider {


    /**
     * 提供的物模型的productCode
     */
    default Set<String> getProvides() {
        return new HashSet<>();
    }


    /**
     * 消费的物模型的productCode
     * 消费的服务，订阅的事件
     */
    default Set<String> getConsumes() {
        return new HashSet<>();
    }

    /**
     * 订阅列表
     */
    default Set<String> getSubscribes() {
        return new HashSet<>();
    }
}
