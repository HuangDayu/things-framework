package cn.huangdayu.things.common.dsl;

import cn.huangdayu.things.common.dsl.template.ThingsInfo;
import cn.huangdayu.things.common.dsl.usecase.ThingsUseCase;
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
        this.useCaseDsl = new HashSet<>();
        this.thingsDsl = new HashSet<>();
    }

    private Set<ThingsUseCase> useCaseDsl;
    private Set<ThingsInfo> thingsDsl;

}
