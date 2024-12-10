package cn.huangdayu.things.common.dsl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author huangdayu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomainSubscribeInfo implements Serializable {


    private String productCode;
    private String eventIdentifier;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainSubscribeInfo that = (DomainSubscribeInfo) o;
        return Objects.equals(productCode, that.productCode) && Objects.equals(eventIdentifier, that.eventIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCode, eventIdentifier);
    }
}
