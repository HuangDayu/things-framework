package cn.huangdayu.things.gateway.loadbalancer;



import java.util.List;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 一致性哈希（Consistent Hashing）
 * @author huangdayu
 */
public class ConsistentHashingBalancer implements LoadBalancer {

    private final ConcurrentSkipListMap<Integer, String> hashRing = new ConcurrentSkipListMap<>();
    private final int numberOfReplicas = 100;

    public ConsistentHashingBalancer(List<String> servers) {
        for (String server : servers) {
            for (int i = 0; i < numberOfReplicas; i++) {
                String key = server + "#" + i;
                int hash = hash(key);
                hashRing.put(hash, server);
            }
        }
    }

    private int hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(key.getBytes());
            int hash = ((digest[3] & 0xFF) << 24) | ((digest[2] & 0xFF) << 16) | ((digest[1] & 0xFF) << 8) | (digest[0] & 0xFF);
            return Math.abs(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing key", e);
        }
    }

    @Override
    public String getNextServer(List<String> servers, String clientId) {
        int hash = hash(clientId);
        Map.Entry<Integer, String> entry = hashRing.ceilingEntry(hash);
        if (entry == null) {
            entry = hashRing.firstEntry();
        }
        return entry.getValue();
    }
}
