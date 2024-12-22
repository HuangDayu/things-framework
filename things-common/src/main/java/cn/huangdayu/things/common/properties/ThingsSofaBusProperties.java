package cn.huangdayu.things.common.properties;

import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import lombok.Data;

import java.util.Map;
import java.util.Objects;

/**
 * @author huangdayu
 */
@Data
public class ThingsSofaBusProperties {

    private String name;

    private boolean enable = true;

    private ThingsSofaBusType type;

    private String server;

    private String clientId;

    private String groupId;

    private String topic;

    private String userName;

    private String password;

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
