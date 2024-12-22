package cn.huangdayu.things.engine.chaining;

import cn.huangdayu.things.api.message.ThingsHandling;
import cn.huangdayu.things.common.annotation.ThingsHandler;
import cn.huangdayu.things.common.async.ThingsAsyncManager;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

import static cn.huangdayu.things.common.enums.ThingsStreamingType.INPUTTING;

/**
 * 处理异步消息的回复
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsHandler(order = 0, source = INPUTTING)
public class ThingsResponseHandling implements ThingsHandling {

    @Override
    public boolean canHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        return StrUtil.isNotBlank(thingsRequest.getJtm().getBaseMetadata().getErrorCode());
    }

    @Override
    public void doHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        ThingsAsyncManager.asAsyncResponse(thingsRequest, thingsResponse);
    }
}
