package cn.huangdayu.things.common.dsl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * @author huangdayu
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DslInfo implements Serializable {

    private Set<DomainInfo> domainDsl;
    private Set<ThingsInfo> thingsDsl;

}
