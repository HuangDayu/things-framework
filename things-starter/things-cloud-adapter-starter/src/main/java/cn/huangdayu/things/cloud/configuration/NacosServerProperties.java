/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.huangdayu.things.cloud.configuration;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;


@Data
@Slf4j
@ConditionalOnProperty(prefix = "spring.cloud.nacos.discovery", name = "server-addr")
@ConfigurationProperties("spring.cloud.nacos.discovery")
public class NacosServerProperties {

    /**
     * nacos discovery server address.
     */
    private String serverAddr;

    /**
     * the nacos authentication username.
     */
    private String username;

    /**
     * the nacos authentication password.
     */
    private String password;

    /**
     * the domain name of a service, through which the server address can be dynamically
     * obtained.
     */
    private String endpoint;

    /**
     * namespace, separation registry of different environments.
     */
    private String namespace;

    /**
     * watch delay,duration to pull new service from nacos server.
     */
    private long watchDelay = 30000;

    /**
     * nacos naming log file name.
     */
    private String logName;

    /**
     * service name to registry.
     */
    @Value("${spring.cloud.nacos.discovery.service:${spring.application.name:}}")
    private String service;

    /**
     * weight for service instance, the larger the value, the larger the weight.
     */
    private float weight = 1;

    /**
     * cluster name for nacos .
     */
    private String clusterName;

    /**
     * group name for nacos.
     */
    private String group = "DEFAULT_GROUP";

    /**
     * naming load from local cache at application start. true is load.
     */
    private String namingLoadCacheAtStart = "false";

    /**
     * extra metadata to register.
     */
    private Map<String, String> metadata = new HashMap<>();

    /**
     * if you just want to subscribe, but don't want to register your service, set it to
     * false.
     */
    private boolean registerEnabled = true;

    /**
     * The ip address your want to register for your service instance, needn't to set it
     * if the auto detect ip works well.
     */
    private String ip;

    /**
     * which network interface's ip you want to register.
     */
    private String networkInterface = "";

    /**
     * choose IPv4 or IPv6,if you don't set it will choose IPv4.
     * When IPv6 is chosen but no IPv6 can be found, system will automatically find IPv4 to ensure there is an
     * available service address.
     */
    private String ipType;

    /**
     * The port your want to register for your service instance, needn't to set it if the
     * auto detect port works well.
     */
    private int port = -1;

    /**
     * whether your service is a https service.
     */
    private boolean secure = false;

    /**
     * access key for namespace.
     */
    private String accessKey;

    /**
     * secret key for namespace.
     */
    private String secretKey;

    /**
     * Heart beat interval. Time unit: millisecond.
     */
    private Integer heartBeatInterval;

    /**
     * Heart beat timeout. Time unit: millisecond.
     */
    private Integer heartBeatTimeout;

    /**
     * Ip delete timeout. Time unit: millisecond.
     */
    private Integer ipDeleteTimeout;

    /**
     * If instance is enabled to accept request. The default value is true.
     */
    private boolean instanceEnabled = true;

    /**
     * If instance is ephemeral.The default value is true.
     */
    private boolean ephemeral = true;

    /**
     * Whether to enable nacos failure tolerance. If enabled, nacos will return cached
     * values when exceptions occur.
     */
    private boolean failureToleranceEnabled;

    /**
     * Throw exceptions during service registration if true, otherwise, log error
     * (defaults to true).
     */
    private boolean failFast = true;

}
