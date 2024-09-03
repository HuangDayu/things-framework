package cn.huangdayu.things.gateway.loadbalancer;



import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

/**
 * 基于响应时间（Response Time Based）
 * @author huangdayu
 */
public class ResponseTimeBasedBalancer implements LoadBalancer {

    private final Map<String, Double> responseTimes = new HashMap<>();

    @Override
    public String getNextServer(List<String> servers, String clientId) {
        servers.sort(Comparator.comparingDouble(server -> responseTimes.getOrDefault(server, Double.MAX_VALUE)));

        String server = servers.get(0);
        return server;
    }
}
