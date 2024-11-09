package cn.huangdayu.things.broker.loadbalancer;

import java.util.List;

public interface LoadBalancer {

    String getNextServer(List<String> servers, String clientId);
}
