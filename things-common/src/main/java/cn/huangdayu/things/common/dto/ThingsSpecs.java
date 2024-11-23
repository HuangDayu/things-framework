package cn.huangdayu.things.common.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThingsSpecs implements Serializable {
    private String unit;
    private String min;
    private String unitName;
    private String size;
    private String max;
    private String step;
}