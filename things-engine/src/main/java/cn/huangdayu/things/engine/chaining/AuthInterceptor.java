package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.engine.annotation.ThingsInterceptor;
import cn.huangdayu.things.engine.chaining.interceptor.Interceptor;
import cn.huangdayu.things.engine.wrapper.ThingsServlet;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsInterceptor
public class AuthInterceptor implements Interceptor {

    @Override
    public boolean doIntercept(ThingsServlet thingsServlet) {
        return true;
    }

}
