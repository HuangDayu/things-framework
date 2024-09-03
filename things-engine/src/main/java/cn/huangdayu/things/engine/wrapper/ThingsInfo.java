package cn.huangdayu.things.engine.wrapper;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class ThingsInfo implements Serializable {
    private String schema;
    private ThingsProfile profile;
    private Set<ThingsServiceInfo> services;
    private Set<ThingsParamInfo> properties;
    private Set<ThingsEventInfo> events;
}