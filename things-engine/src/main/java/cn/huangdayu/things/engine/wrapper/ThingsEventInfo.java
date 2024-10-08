package cn.huangdayu.things.engine.wrapper;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class ThingsEventInfo implements Serializable {
    private Set<ThingsParamInfo> outputData;
    private String identifier;
    private String name;
    private String type;
    private String desc;
}