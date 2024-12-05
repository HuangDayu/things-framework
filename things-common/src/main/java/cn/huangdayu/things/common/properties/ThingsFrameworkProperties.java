package cn.huangdayu.things.common.properties;

import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.Data;

import java.util.Set;

/**
 * @author huangdayu
 */
@Data
public class ThingsFrameworkProperties {

    public ThingsFrameworkProperties() {
        this.servers = new ConcurrentHashSet<>();
        this.instance = new ThingsInstance();
    }

    /**
     * 其他服务列表
     */
    private Set<String> servers;


    /**
     * 本实例信息
     */
    private volatile ThingsInstance instance;
}
