package cn.huangdayu.things.broker.loadbalancer;



import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 最少连接（Least Connections）
 *
 * @author huangdayu
 */
public class LeastConnectionsBalancer implements LoadBalancer {

    private final Map<String, Integer> connections = new HashMap<>();

    @Override
    public String getNextServer(List<String> servers, String clientId) {
        servers.sort(Comparator.comparingInt(server -> connections.getOrDefault(server, 0)));
        String server = servers.get(0);
        connections.merge(server, 1, Integer::sum);
        return server;
    }
}
