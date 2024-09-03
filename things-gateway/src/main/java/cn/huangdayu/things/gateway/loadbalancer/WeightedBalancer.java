package cn.huangdayu.things.gateway.loadbalancer;



import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

/**
 * 权重（Weighted）
 * @author huangdayu
 */
public class WeightedBalancer implements LoadBalancer {

    private final Map<String, Integer> weights = new HashMap<>();
    private final Random random = new Random();

    public WeightedBalancer(List<String> servers, Map<String, Integer> weights) {
        this.weights.putAll(weights);
    }

    @Override
    public String getNextServer(List<String> servers, String clientId) {
        int totalWeight = servers.stream().mapToInt(server -> weights.getOrDefault(server, 1)).sum();
        int randomWeight = random.nextInt(totalWeight);
        int currentWeight = 0;

        for (String server : servers) {
            currentWeight += weights.getOrDefault(server, 1);
            if (currentWeight > randomWeight) {
                return server;
            }
        }

        return servers.get(0); // Fallback to the first server
    }
}
