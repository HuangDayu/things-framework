package cn.huangdayu.things.mcp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author huangdayu
 */
@Data
@ConfigurationProperties(prefix = "things.mcp")
public class ThingsMcpProperties {

    public ThingsMcpProperties() {
        this.resources = new CopyOnWriteArrayList<>();
    }

    private List<Resource> resources;


    @Data
    private static class Resource {
        private String uri;
        private String name;
        private String title;
        private String description;
        private String mimeType;
        private Long size;
        private Annotations annotations;
    }

    @Data
    private static class Annotations {
        private List<Role> audience;
        private Double priority;
    }

    private static enum Role {
        USER,
        ASSISTANT;
    }

}
