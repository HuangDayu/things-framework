package cn.huangdayu.things.starter;

import cn.huangdayu.things.api.instances.ThingsInstancesServer;
import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.net.NetUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * @author huangdayu
 */
@Component
@RequiredArgsConstructor
public class ThingsInstancesSpringServer implements ThingsInstancesServer {
    private final Environment environment;

    @Override
    public String getServerHost() {
        return getIp() + ":" + environment.getProperty("server.port");
    }

    @Override
    public String getServerName() {
        return environment.getProperty("spring.application.name");
    }

    private String getIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return NetUtil.getIpByHost(Ipv4Util.LOCAL_IP);
        }
    }
}
