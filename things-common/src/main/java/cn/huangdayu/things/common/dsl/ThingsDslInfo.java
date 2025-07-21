package cn.huangdayu.things.common.dsl;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@Data
public class ThingsDslInfo implements Serializable {

    public ThingsDslInfo() {
        this.domainDsl = new HashSet<>();
        this.thingsDsl = new HashSet<>();
    }

    private Set<DomainInfo> domainDsl;
    private Set<ThingsTemplate> thingsDsl;

}
