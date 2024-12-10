package cn.huangdayu.things.common.dsl;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThingsParamInfo implements Serializable {
    private String identifier;
    private ThingsDataType dataType;
    private String name;
    private String description;
    private String accessMode;
}