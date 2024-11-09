package cn.huangdayu.things.broker.loadbalancer;



import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

/**
 * 最少活跃数（Least Active）
 * @author huangdayu
 */
public class LeastActiveBalancer implements LoadBalancer {

    private final Map<String, Integer> activeRequests = new HashMap<>();

    @Override
    public String getNextServer(List<String> servers, String clientId) {
        servers.sort(Comparator.comparingInt(server -> activeRequests.getOrDefault(server, 0)));

        String server = servers.get(0);
        activeRequests.merge(server, 1, Integer::sum);
        return server;
    }
}
