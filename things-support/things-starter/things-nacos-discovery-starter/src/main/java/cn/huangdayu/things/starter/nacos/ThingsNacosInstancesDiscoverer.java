package cn.huangdayu.things.starter.nacos;

import cn.huangdayu.things.api.endpoint.ThingsEndpointFactory;
import cn.huangdayu.things.api.instances.ThingsInstancesDiscoverer;
import cn.huangdayu.things.api.instances.ThingsInstancesManager;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.observer.event.ThingsInstancesChangedEvent;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
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

import static cn.huangdayu.things.api.endpoint.ThingsEndpointFactory.RESTFUL_SCHEMA;

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
    private final ThingsInstancesManager thingsInstancesManager;
    private final ThingsEndpointFactory thingsEndpointFactory;
    private final ThingsFrameworkProperties thingsFrameworkProperties;
    public static final String METADATA_INSTANCES_CODE = "things-instances-code";
    public static final String METADATA_INSTANCES_SIZE = "things-instances-size";
    public static final String METADATA_INSTANCES_TYPE = "things-instances-type";
    public static final Set<String> SUBSCRIBED_SERVERS = new ConcurrentHashSet<>();

    @SneakyThrows
    public ThingsNacosInstancesDiscoverer(ThingsInstancesManager thingsInstancesManager, ThingsEventObserver thingsEventObserver,
                                          NacosServerProperties nacosServerProperties, ThingsEndpointFactory thingsEndpointFactory,
                                          ThingsFrameworkProperties thingsFrameworkProperties) {
        this.thingsInstancesManager = thingsInstancesManager;
        this.thingsEndpointFactory = thingsEndpointFactory;
        this.thingsFrameworkProperties = thingsFrameworkProperties;
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
            ThingsInstance thingsInstance = thingsFrameworkProperties.getInstance();
            if (thingsInstance.getEndpointUri().contains(instance.getIp() + ":" + instance.getPort())) {
                instance.getMetadata().put(METADATA_INSTANCES_CODE, thingsInstance.getCode());
                instance.getMetadata().put(METADATA_INSTANCES_SIZE, String.valueOf(thingsInstancesManager.getInstancesSize()));
                instance.getMetadata().put(METADATA_INSTANCES_TYPE, JSON.toJSONString(thingsInstance.getTypes()));
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
                instances.parallelStream().filter(instance ->
                        StrUtil.isAllNotBlank(instance.getMetadata().get(METADATA_INSTANCES_CODE), instance.getMetadata().get(METADATA_INSTANCES_TYPE))
                                && CollUtil.isNotEmpty(JSON.parseArray(instance.getMetadata().get(METADATA_INSTANCES_TYPE)))).forEach(instance -> {
                    servers.add(RESTFUL_SCHEMA + instance.getIp() + ":" + instance.getPort());
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
                        thingsEventObserver.notifyObservers(new ThingsInstancesChangedEvent(event, addedThingsInstances, removedInstanceCodes));
                    }
                    return;
                }
            }
            thingsEventObserver.notifyObservers(new ThingsInstancesChangedEvent(event, getThingsInstances(namingEvent.getInstances()), Collections.emptySet()));
        }
    }


    private Set<ThingsInstance> getThingsInstances(List<Instance> instances) {
        return getAllThingsInstance(getInstanceServers(instances));
    }

    private Set<String> getInstanceCodes(List<Instance> instances) {
        if (CollUtil.isNotEmpty(instances)) {
            return instances.parallelStream()
                    .filter(instance -> StrUtil.isNotBlank(instance.getMetadata().get(METADATA_INSTANCES_CODE)))
                    .map(instance -> instance.getMetadata().get(METADATA_INSTANCES_CODE)).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    private Set<String> getInstanceServers(List<Instance> instances) {
        if (CollUtil.isNotEmpty(instances)) {
            return instances.parallelStream()
                    .filter(instance -> StrUtil.isNotBlank(instance.getMetadata().get(METADATA_INSTANCES_CODE)))
                    .map(instance -> RESTFUL_SCHEMA + instance.getIp() + ":" + instance.getPort()).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    private Set<ThingsInstance> getAllThingsInstance(Set<String> servers) {
        Set<ThingsInstance> thingsInstances = new ConcurrentHashSet<>();
        if (CollUtil.isEmpty(servers)) {
            return thingsInstances;
        }
        ThingsInstance thingsInstance = this.thingsFrameworkProperties.getInstance();
        for (String server : servers) {
            try {
                if (server.equals(thingsInstance.getEndpointUri())) {
                    continue;
                }
                thingsInstances.add(thingsEndpointFactory.create(server).exchange(thingsInstance));
            } catch (Exception e) {
                log.error("Get Things instances to {} server exception : ", server, e);
            }
        }
        return thingsInstances;
    }
}
