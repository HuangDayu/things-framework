package cn.huangdayu.things.mcp.services;

import cn.huangdayu.things.api.infrastructure.ThingsQueryService;
import cn.huangdayu.things.common.dsl.device.DeviceInfo;
import cn.huangdayu.things.common.dsl.template.ThingsInfo;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * @author huangdayu
 */
@Slf4j
@Service
public class MockThingsQueryService implements ThingsQueryService {

    private static final Set<DeviceInfo> deviceInfos = new CopyOnWriteArraySet<>();
    private static final Set<ThingsInfo> thingsInfos = new CopyOnWriteArraySet<>();

    @SneakyThrows
    @PostConstruct
    public void init() {
        initResources();
    }


    @Override
    public Set<DeviceInfo> queryDeviceInfo(String deviceName, String deviceType, String deviceLocation) {
        log.debug("Things MCP Tool query device info ,deviceName: {} , deviceType: {} , deviceLocation: {}", deviceName, deviceType, deviceLocation);
        return deviceInfos.stream().filter(deviceInfo -> queryDeviceInfo(deviceInfo, deviceName, deviceType, deviceLocation)).collect(Collectors.toSet());
    }

    @Override
    public ThingsInfo queryThingsInfo(String productCode, String deviceCode) {
        log.debug("Things MCP Tool query things info ,productCode: {} , deviceCode: {}", productCode, deviceCode);
        Optional<ThingsInfo> first = thingsInfos.stream().filter(thingsInfo ->
                (StrUtil.isNotBlank(productCode) && thingsInfo.getProfile().getProductCode().equalsIgnoreCase(productCode)) ||
                        (StrUtil.isNotBlank(deviceCode) && thingsInfo.getProfile().getDeviceId().equalsIgnoreCase(deviceCode))).findFirst();
        return first.orElseGet(() -> null);
    }

    private boolean queryDeviceInfo(DeviceInfo deviceInfo, String deviceName, String deviceType, String deviceLocation) {
        if (!isSimilar(deviceInfo.getDeviceLocation(), deviceLocation)) {
            return false;
        }
        if (!isSimilar(deviceInfo.getDeviceName(), deviceName)) {
            return false;
        }
        if (!isSimilar(deviceInfo.getDeviceType(), deviceType)) {
            return false;
        }
        return true;
    }

    private boolean isSimilar(String field1, String field2) {
        if (StrUtil.isBlank(field2)) {
            return true;
        }
        return field1 != null && (field1.contains(field2) || field2.contains(field1));
    }


    private void initResources() throws IOException {
        ClassPathResource resource = new ClassPathResource("things-examples/");
        File folder = resource.getFile();
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    ClassPathResource fileResource = new ClassPathResource("things-examples/" + file.getName());
                    if (file.getName().endsWith("-device.json")) {
                        deviceInfos.add(JSON.parseObject(fileResource.getContentAsString(StandardCharsets.UTF_8), DeviceInfo.class));
                    } else {
                        thingsInfos.add(JSON.parseObject(fileResource.getContentAsString(StandardCharsets.UTF_8), ThingsInfo.class));
                    }
                }
            }
        }
    }
}
