package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.api.container.ThingsContainer;
import cn.huangdayu.things.api.message.ThingsHandling;
import cn.huangdayu.things.common.annotation.ThingsHandler;
import cn.huangdayu.things.common.enums.ThingsChainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author huangdayu
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThingsHandlers {

    private ThingsContainer thingsContainer;
    private ThingsHandler thingsHandler;
    private ThingsHandling thingsHandling;
    private ThingsChainingType chainingType;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ThingsHandlers that = (ThingsHandlers) o;
        return Objects.equals(thingsHandling, that.thingsHandling);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(thingsHandling);
    }
}
