package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;

/**
 * @author huangdayu
 */
public interface ThingsHandling {

    /**
     * 同步处理消息
     *
     * @param thingsRequest
     * @return
     */
    void doHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse);
}
