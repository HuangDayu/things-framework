package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.common.annotation.ThingsIntercepting;
import cn.huangdayu.things.engine.chaining.interceptor.ThingsInterceptor;
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

    private ThingsIntercepting thingsIntercepting;
    private ThingsInterceptor thingsInterceptor;

}
