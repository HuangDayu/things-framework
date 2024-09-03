package cn.huangdayu.things.gateway.loadbalancer;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 粘滞会话（Sticky Sessions）
 *
 * @author huangdayu
 */
public class StickySessionsBalancer implements LoadBalancer {

    private final Map<String, String> clientToServer = new HashMap<>();

    @Override
    public String getNextServer(List<String> servers, String clientId) {
        String server = clientToServer.get(clientId);
        if (server == null) {
            server = servers.get(0); // Fallback to the first server
            clientToServer.put(clientId, server);
        }
        return server;
    }
}
