package cn.huangdayu.things.common.dsl;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class ThingsProfile implements Serializable {
    private String schema;
    private ThingsProfileInfo tenant;
    private ThingsProfileInfo application;
    private ThingsProfileInfo product;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThingsProfile that = (ThingsProfile) o;
        return Objects.equals(schema, that.schema) && Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schema, product);
    }
}