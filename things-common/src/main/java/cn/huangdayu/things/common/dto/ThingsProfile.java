package cn.huangdayu.things.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class ThingsProfile implements Serializable {
    private String productCode;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ThingsProfile that = (ThingsProfile) o;
        return Objects.equals(productCode, that.productCode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productCode);
    }
}