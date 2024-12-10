package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsInterceptor;
import cn.huangdayu.things.common.annotation.ThingsIntercepting;
import cn.huangdayu.things.common.wrapper.ThingsServlet;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsIntercepting
public class CheckThingsInterceptor implements ThingsInterceptor {

    @Override
    public boolean doIntercept(ThingsServlet thingsServlet) {
        return StrUtil.isAllNotBlank(thingsServlet.getJtm().getBaseMetadata().getProductCode());
    }

}
