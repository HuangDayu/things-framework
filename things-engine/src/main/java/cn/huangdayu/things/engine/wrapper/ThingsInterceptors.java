package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.api.container.ThingsContainer;
import cn.huangdayu.things.api.message.ThingsIntercepting;
import cn.huangdayu.things.common.annotation.ThingsInterceptor;
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
public class ThingsInterceptors {

    private ThingsContainer thingsContainer;
    private ThingsInterceptor thingsInterceptor;
    private ThingsIntercepting thingsIntercepting;
    private ThingsChainingType chainingType;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ThingsInterceptors that = (ThingsInterceptors) o;
        return Objects.equals(thingsIntercepting, that.thingsIntercepting);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(thingsIntercepting);
    }
}
