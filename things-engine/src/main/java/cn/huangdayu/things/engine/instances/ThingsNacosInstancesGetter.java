package cn.huangdayu.things.engine.instances;

import cn.huangdayu.things.engine.annotation.ThingsBean;
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
import cn.huangdayu.things.engine.async.ThingsInstancesChangeEvent;
import cn.huangdayu.things.engine.core.ThingsInstancesEngine;
import cn.huangdayu.things.engine.core.ThingsObserverEngine;
import cn.huangdayu.things.engine.core.executor.ThingsInstancesExecutor;
import cn.huangdayu.things.engine.wrapper.ThingsInstance;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author huangdayu
 */
@ConditionalOnProperty(prefix = "spring.cloud.nacos.discovery", name = "server-addr")
@ThingsBean
public class ThingsNacosInstancesGetter extends ThingsRestfulInstancesGetter implements EventListener, ThingsInstancesGetter {

    private final NamingService namingService;
    private final NacosServerProperties nacosServerProperties;
    private final NamingMaintainService namingMaintainService;
    private final ThingsObserverEngine thingsObserverEngine;
    public static final String METADATA_INSTANCES = "things-engine-instances";
    public static final String METADATA_INSTANCES_SIZE = "things-engine-instances-size";
    public static final Set<String> SUBSCRIBED_SERVERS = new ConcurrentHashSet<>();

    @SneakyThrows
    public ThingsNacosInstancesGetter(ThingsInstancesEngine thingsInstancesEngine, ThingsObserverEngine thingsObserverEngine, NacosServerProperties nacosServerProperties) {
        super(thingsInstancesEngine);
        Properties properties = new Properties();
        properties.putAll((JSONObject) JSON.toJSON(nacosServerProperties));
        this.nacosServerProperties = nacosServerProperties;
        this.namingService = NacosFactory.createNamingService(properties);
        this.namingMaintainService = NacosFactory.createMaintainService(properties);
        this.thingsObserverEngine = thingsObserverEngine;
    }


    @Scheduled(initialDelay = 10, fixedDelay = 30_000, scheduler = "thingsTaskScheduler")
    @SneakyThrows
    private void updateMetaData() {
        List<Instance> allInstances = namingService.selectInstances(nacosServerProperties.getService(), nacosServerProperties.getGroup(), true);
        for (Instance instance : allInstances) {
            ThingsInstance thingsInstance = thingsInstancesEngine.getThingsInstance();
            if (thingsInstance.getServer().equals(instance.getIp() + ":" + instance.getPort())) {
                instance.getMetadata().put(METADATA_INSTANCES, thingsInstance.getCode());
                instance.getMetadata().put(METADATA_INSTANCES_SIZE, String.valueOf(ThingsInstancesExecutor.getInstancesSize()));
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
                        thingsObserverEngine.notifyObservers(new ThingsInstancesChangeEvent(event, addedThingsInstances, removedInstanceCodes));
                    }
                    return;
                }
            }
            thingsObserverEngine.notifyObservers(new ThingsInstancesChangeEvent(event, getThingsInstances(namingEvent.getInstances()), Collections.emptySet()));
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
}
