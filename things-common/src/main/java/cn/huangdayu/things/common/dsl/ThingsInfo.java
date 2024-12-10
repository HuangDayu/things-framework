package cn.huangdayu.things.common.dsl;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Data
public class ThingsInfo implements Serializable {
    private ThingsProfile profile;
    private Set<ThingsServiceInfo> services;
    private Set<ThingsParamInfo> properties;
    private Set<ThingsEventInfo> events;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ThingsInfo that = (ThingsInfo) o;
        return Objects.equals(profile, that.profile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profile);
    }
}