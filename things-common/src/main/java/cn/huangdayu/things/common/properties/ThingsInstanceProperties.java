package cn.huangdayu.things.common.properties;

import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.Data;

import java.util.Set;

/**
 * @author huangdayu
 */
@Data
public class ThingsInstanceProperties {

    public ThingsInstanceProperties() {
        this.servers = new ConcurrentHashSet<>();
        this.instance = new ThingsInstance();
    }

    /**
     * 其他服务列表
     */
    private Set<String> servers;

    /**
     * 软总线服务列表
     */
    private Set<ThingsSofaBusProperties> sofaBus;


    /**
     * 本实例信息
     */
    private volatile ThingsInstance instance;
}
