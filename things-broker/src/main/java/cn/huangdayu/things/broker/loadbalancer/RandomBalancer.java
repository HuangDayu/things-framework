package cn.huangdayu.things.broker.loadbalancer;



import java.util.List;
import java.util.Random;

/**
 * 随机（Random）
 *
 * @author huangdayu
 */
public class RandomBalancer implements LoadBalancer {

    private final Random random = new Random();

    @Override
    public String getNextServer(List<String> servers, String clientId) {
        int index = random.nextInt(servers.size());
        return servers.get(index);
    }
}
