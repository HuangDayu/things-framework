package cn.huangdayu.things.ai.mcp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;
import java.util.Set;

/**
 * @author huangdayu
 */
@Data
@ConfigurationProperties(prefix = "things.ai")
public class ThingsAiProperties {

    private Set<Models> models;


    @Data
    public static class Models {
        private String model;
        private String supplier;
        private String desc;

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Models models = (Models) o;
            return Objects.equals(model, models.model);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(model);
        }
    }

}
