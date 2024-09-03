package cn.huangdayu.things.gateway.loadbalancer;


import java.util.Iterator;
import java.util.List;

/**
 * 轮询（Round Robin）
 *
 * @author huangdayu
 */
public class RoundRobinBalancer implements LoadBalancer {

    private Iterator<String> iterator;

    public RoundRobinBalancer(List<String> servers) {
        this.iterator = servers.iterator();
    }

    @Override
    public String getNextServer(List<String> servers, String clientId) {
        if (!iterator.hasNext()) {
            iterator = servers.iterator();
        }
        return iterator.next();
    }
}
