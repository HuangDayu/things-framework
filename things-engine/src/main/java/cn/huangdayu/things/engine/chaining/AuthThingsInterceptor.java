package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.common.annotation.ThingsIntercepting;
import cn.huangdayu.things.engine.chaining.interceptor.ThingsInterceptor;
import cn.huangdayu.things.engine.wrapper.ThingsServlet;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsIntercepting
public class AuthThingsInterceptor implements ThingsInterceptor {

    @Override
    public boolean doIntercept(ThingsServlet thingsServlet) {
        return StrUtil.isAllNotBlank(thingsServlet.getMessage().getBaseMetadata().getProductCode());
    }

}
