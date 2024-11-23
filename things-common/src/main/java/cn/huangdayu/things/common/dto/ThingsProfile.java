package cn.huangdayu.things.common.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThingsProfile implements Serializable {
    private String productCode;
    private String name;
}