package cn.huangdayu.things.common.dsl;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author huangdayu
 */
@Data
public class ThingsProfileInfo implements Serializable {

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThingsProfileInfo that = (ThingsProfileInfo) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }
}
