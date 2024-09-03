package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.engine.annotation.ThingsInterceptor;
import cn.huangdayu.things.engine.chaining.interceptor.Interceptor;
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
    private Interceptor interceptor;

}
