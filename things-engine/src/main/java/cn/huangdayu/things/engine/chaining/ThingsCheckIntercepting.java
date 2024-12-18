package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsHandling;
import cn.huangdayu.things.api.message.ThingsIntercepting;
import cn.huangdayu.things.common.annotation.ThingsInterceptor;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import static cn.huangdayu.things.common.enums.ThingsStreamingType.INPUTTING;

/**
 * @author huangdayu
 */
@Slf4j
@ThingsInterceptor(source = INPUTTING)
public class ThingsCheckIntercepting implements ThingsIntercepting {

    @Override
    public boolean preHandle(ThingsRequest request, ThingsResponse response, ThingsHandling handling) {
        return StrUtil.isAllNotBlank(request.getJtm().getBaseMetadata().getProductCode());
    }
}
