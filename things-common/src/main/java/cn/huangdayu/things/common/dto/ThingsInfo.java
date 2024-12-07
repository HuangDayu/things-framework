package cn.huangdayu.things.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Data
public class ThingsInfo implements Serializable {
    private String schema;
    private ThingsProfile profile;
    private Set<ThingsServiceInfo> services;
    private Set<ThingsParamInfo> properties;
    private Set<ThingsEventInfo> events;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ThingsInfo that = (ThingsInfo) o;
        return Objects.equals(schema, that.schema) && Objects.equals(profile, that.profile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schema, profile);
    }
}