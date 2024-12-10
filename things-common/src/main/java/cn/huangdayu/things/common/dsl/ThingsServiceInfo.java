package cn.huangdayu.things.common.dsl;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class ThingsServiceInfo implements Serializable {
    private String identifier;
    private String name;
    private String callType;
    private String desc;
    private Set<ThingsParamInfo> inputData;
    private Set<ThingsParamInfo> outputData;
}