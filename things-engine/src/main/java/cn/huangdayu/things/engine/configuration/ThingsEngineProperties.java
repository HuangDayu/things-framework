package cn.huangdayu.things.engine.configuration;

import cn.huangdayu.things.engine.wrapper.ThingsInstance;
import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * @author huangdayu
 */
@Data
@ConfigurationProperties("wwgy-cloud.things-engine")
public class ThingsEngineProperties {

    public ThingsEngineProperties() {
        this.servers = new ConcurrentHashSet<>();
        this.instance = new ThingsInstance();
    }

    /**
     * 默认得消息上行协议
     * 当消息没有接收者时，使用此协议进行上行发送
     */
    private String upstreamProtocol = "http";

    /**
     * 其他服务列表
     */
    private Set<String> servers;


    /**
     * 本实例信息
     */
    private ThingsInstance instance;
}
