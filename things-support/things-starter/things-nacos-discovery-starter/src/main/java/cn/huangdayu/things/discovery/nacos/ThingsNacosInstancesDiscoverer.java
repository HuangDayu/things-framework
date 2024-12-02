package cn.huangdayu.things.discovery.nacos;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.instances.ThingsInstanceManager;
import cn.huangdayu.things.api.instances.ThingsInstancesDiscoverer;
import cn.huangdayu.things.api.restful.ThingsEndpoint;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.event.ThingsEventObserver;
import cn.huangdayu.things.common.event.ThingsInstancesChangeEvent;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import cn.huangdayu.things.discovery.configuration.NacosServerProperties;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.alibaba.nacos.api.naming.utils.NamingUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author huangdayu
 */
@Slf4j
@ConditionalOnProperty(prefix = "spring.cloud.nacos.discovery", name = "server-addr")
@ThingsBean
public class ThingsNacosInstancesDiscoverer implements EventListener, ThingsInstancesDiscoverer {

    private final NamingService namingService;
    private final NacosServerProperties nacosServerProperties;
    private final NamingMaintainService namingMaintainService;
    private final ThingsEventObserver thingsEventObserver;
    private final ThingsInstanceManager thingsInstanceManager;
    private final ThingsEndpointFactory thingsEndpointFactory;
    public static final String METADATA_INSTANCES = "things-engine-instances";
    public static final String METADATA_INSTANCES_SIZE = "things-engine-instances-size";
    public static final Set<String> SUBSCRIBED_SERVERS = new ConcurrentHashSet<>();

    @SneakyThrows
    public ThingsNacosInstancesDiscoverer(ThingsInstanceManager thingsInstanceManager, ThingsEventObserver thingsEventObserver,
                                          NacosServerProperties nacosServerProperties, ThingsEndpointFactory thingsEndpointFactory) {
        this.thingsInstanceManager = thingsInstanceManager;
        this.thingsEndpointFactory = thingsEndpointFactory;
        Properties properties = new Properties();
        properties.putAll((JSONObject) JSON.toJSON(nacosServerProperties));
        this.nacosServerProperties = nacosServerProperties;
        this.namingService = NacosFactory.createNamingService(properties);
        this.namingMaintainService = NacosFactory.createMaintainService(properties);
        this.thingsEventObserver = thingsEventObserver;
    }


    @Scheduled(initialDelay = 10, fixedDelay = 30_000, scheduler = "thingsTaskScheduler")
    @SneakyThrows
    private void updateMetaData() {
        List<Instance> allInstances = namingService.selectInstances(nacosServerProperties.getService(), nacosServerProperties.getGroup(), true);
        for (Instance instance : allInstances) {
            ThingsInstance thingsInstance = thingsInstanceManager.getThingsInstance();
            if (thingsInstance.getEndpointUri().contains(instance.getIp() + ":" + instance.getPort())) {
                instance.getMetadata().put(METADATA_INSTANCES, thingsInstance.getCode());
                instance.getMetadata().put(METADATA_INSTANCES_SIZE, String.valueOf(thingsInstanceManager.getInstancesSize()));
                namingMaintainService.updateInstance(nacosServerProperties.getService(), nacosServerProperties.getGroup(), instance);
            }
        }
    }


    @SneakyThrows
    @Override
    public Set<ThingsInstance> getAllInstance() {
        Set<String> servers = new ConcurrentHashSet<>();
        ListView<String> servicesOfServer = new ListView<>();
        int i = 1;
        do {
            servicesOfServer = namingService.getServicesOfServer(i++, 100, nacosServerProperties.getGroup());
            for (String serviceName : servicesOfServer.getData()) {
                List<Instance> instances = namingService.getAllInstances(serviceName, nacosServerProperties.getGroup());
                instances.parallelStream().filter(instance -> StrUtil.isNotBlank(instance.getMetadata().get(METADATA_INSTANCES))).forEach(instance -> {
                    servers.add(instance.getIp() + ":" + instance.getPort());
                    addSubscribe(instance);
                });
            }
        } while (CollUtil.isNotEmpty(servicesOfServer.getData()));
        return getAllThingsInstance(servers);
    }

    @SneakyThrows
    private void addSubscribe(Instance instance) {
        String key = instance.getServiceName();
        if (!SUBSCRIBED_SERVERS.contains(key)) {
            namingService.subscribe(NamingUtils.getServiceName(instance.getServiceName()), NamingUtils.getGroupName(instance.getServiceName()), this);
            SUBSCRIBED_SERVERS.add(key);
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof NamingEvent namingEvent) {
            if ("NamingChangeEvent".equals(event.getClass().getSimpleName())) {
                Object instancesDiff = ReflectUtil.getFieldValue(event, "instancesDiff");
                if (instancesDiff != null) {
                    List<Instance> addedInstances = (List<Instance>) ReflectUtil.getFieldValue(instancesDiff, "addedInstances");
                    List<Instance> removedInstances = (List<Instance>) ReflectUtil.getFieldValue(instancesDiff, "removedInstances");
                    Set<ThingsInstance> addedThingsInstances = getThingsInstances(addedInstances);
                    Set<String> removedInstanceCodes = getInstanceCodes(removedInstances);
                    if (CollUtil.isNotEmpty(addedThingsInstances) || CollUtil.isNotEmpty(removedInstanceCodes)) {
                        thingsEventObserver.notifyObservers(new ThingsInstancesChangeEvent(event, addedThingsInstances, removedInstanceCodes));
                    }
                    return;
                }
            }
            thingsEventObserver.notifyObservers(new ThingsInstancesChangeEvent(event, getThingsInstances(namingEvent.getInstances()), Collections.emptySet()));
        }
    }


    private Set<ThingsInstance> getThingsInstances(List<Instance> instances) {
        return getAllThingsInstance(getInstanceServers(instances));
    }

    private Set<String> getInstanceCodes(List<Instance> instances) {
        if (CollUtil.isNotEmpty(instances)) {
            return instances.parallelStream()
                    .filter(instance -> StrUtil.isNotBlank(instance.getMetadata().get(METADATA_INSTANCES)))
                    .map(instance -> instance.getMetadata().get(METADATA_INSTANCES)).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    private Set<String> getInstanceServers(List<Instance> instances) {
        if (CollUtil.isNotEmpty(instances)) {
            return instances.parallelStream()
                    .filter(instance -> StrUtil.isNotBlank(instance.getMetadata().get(METADATA_INSTANCES)))
                    .map(instance -> instance.getIp() + ":" + instance.getPort()).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    private Set<ThingsInstance> getAllThingsInstance(Set<String> servers) {
        Set<ThingsInstance> thingsInstances = new ConcurrentHashSet<>();
        if (CollUtil.isEmpty(servers)) {
            return thingsInstances;
        }
        ThingsInstance thingsInstance = this.thingsInstanceManager.getThingsInstance();
        for (String server : servers) {
            try {
                if (server.equals(thingsInstance.getEndpointUri())) {
                    continue;
                }
                thingsInstances.add(thingsEndpointFactory.create(ThingsEndpoint.class, server).exchange(thingsInstance));
            } catch (Exception e) {
                log.error("Get Things instances to {} server exception : {}", server, e.getMessage());
            }
        }
        return thingsInstances;
    }
}
