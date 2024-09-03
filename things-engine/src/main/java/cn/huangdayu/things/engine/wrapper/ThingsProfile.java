package cn.huangdayu.things.engine.wrapper;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThingsProfile implements Serializable {
    private String productCode;
    private String name;
}