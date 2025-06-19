package cn.huangdayu.things.common.properties;

import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import lombok.Data;

import java.util.*;

/**
 * @author huangdayu
 */
@Data
public class ThingsEngineProperties {

    public ThingsEngineProperties() {
        this.code = UUID.randomUUID().toString().replace("-", "");
        this.configPath = "temp/things/config/" + code + "/config.json";
        this.sofaArk = new ThingsSofaArkProperties();
        this.sofaBiz = new ThingsSofaBizProperties();
        this.sofaBus = new HashSet<>();
    }

    private String code;

    private String configPath;

    /**
     * 软总线服务列表
     */
    private Set<ThingsSofaBusProperties> sofaBus;

    private ThingsSofaArkProperties sofaArk;

    private ThingsSofaBizProperties sofaBiz;

    /**
     * @author huangdayu
     */
    @Data
    public static class ThingsSofaBusProperties {

        private String name;

        private boolean enable = true;

        private ThingsSofaBusType type;

        private String server;

        private String clientId;

        private String groupId;

        private String topic;

        private String userName;

        private String password;

        private String persistenceDir = "temp/sofabus/";

        private Map<String, String> properties;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ThingsSofaBusProperties that = (ThingsSofaBusProperties) o;
            return type == that.type && Objects.equals(server, that.server) && Objects.equals(clientId, that.clientId) && Objects.equals(groupId, that.groupId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, server, clientId, groupId);
        }
    }

    /**
     * @author huangdayu
     */
    @Data
    public static class ThingsSofaArkProperties {

        private boolean enableArkAutoManage = true;

        private String arkBizDirectory = "ark-biz";


        private String arkBizJarEndsWith = "-biz.jar";

    }

    @Data
    public static class ThingsSofaBizProperties {

        /**
         * biz可获取ark的bean的包路径
         */
        private Set<String> sharedBeanPackages;

        /**
         * biz获取ark的bean的类（全路径）
         */
        private Set<String> sharedBeanClasses;

        /**
         * biz获取ark的bean的名称
         */
        private Set<String> sharedBeanNames;

    }
}
