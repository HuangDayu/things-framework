package cn.huangdayu.things.common.dsl.usecase;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author huangdayu
 */
@Data
public class ThingsUseCaseInfo implements Serializable {

    private String schema;
    private String name;
    private String code;
    private String version;
    private String description;
    private String icon;
    private String type;
    private String manufacturer;
    private String model;
    private String category;
    private String certification;
    private String tenantCode;
    private String applicationCode;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThingsUseCaseInfo that = (ThingsUseCaseInfo) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }
}
