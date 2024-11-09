package cn.huangdayu.things.broker.loadbalancer;

public enum LoadBalancingStrategy {
    ROUND_ROBIN,
    RANDOM,
    LEAST_CONNECTIONS,
    WEIGHTED,
    CONSISTENT_HASHING,
    RESPONSE_TIME_BASED,
    STICKY_SESSIONS,
    LEAST_ACTIVE
}
