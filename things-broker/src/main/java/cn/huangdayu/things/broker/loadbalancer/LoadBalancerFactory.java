package cn.huangdayu.things.broker.loadbalancer;

import java.util.List;
import java.util.Map;

public class LoadBalancerFactory {

    public static LoadBalancer createBalancer(LoadBalancingStrategy strategy, List<String> servers, Map<String, Integer> weights, String clientId) {
        return switch (strategy) {
            case ROUND_ROBIN -> new RoundRobinBalancer(servers);
            case RANDOM -> new RandomBalancer();
            case LEAST_CONNECTIONS -> new LeastConnectionsBalancer();
            case WEIGHTED -> new WeightedBalancer(servers, weights);
            case CONSISTENT_HASHING -> new ConsistentHashingBalancer(servers);
            case RESPONSE_TIME_BASED -> new ResponseTimeBasedBalancer();
            case STICKY_SESSIONS -> new StickySessionsBalancer();
            case LEAST_ACTIVE -> new LeastActiveBalancer();
        };
    }
}
