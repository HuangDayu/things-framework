package cn.huangdayu.things.common.dsl;

import cn.huangdayu.things.common.wrapper.ThingsInstance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * @author huangdayu
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DslInfo implements Serializable {

    private ThingsInstance instance;
    private Set<DomainInfo> domainDsl;
    private Set<ThingsInfo> thingsDsl;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DslInfo dslInfo = (DslInfo) o;
        return Objects.equals(instance, dslInfo.instance);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(instance);
    }
}
