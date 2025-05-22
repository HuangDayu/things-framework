package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.api.message.ThingsIntercepting;
import cn.huangdayu.things.common.annotation.ThingsInterceptor;
import cn.huangdayu.things.common.enums.ThingsChainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThingsInterceptors {

    private ThingsInterceptor thingsInterceptor;
    private ThingsIntercepting thingsIntercepting;
    private ThingsChainingType chainingType;

}
