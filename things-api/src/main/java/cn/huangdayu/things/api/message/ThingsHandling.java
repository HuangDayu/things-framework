package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;

/**
 * @author huangdayu
 */
public interface ThingsHandling {

    /**
     * 判断是否可以处理消息
     * @param thingsRequest
     * @param thingsResponse
     * @return
     */
    default boolean canHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        return true;
    }


    /**
     * 处理消息
     *
     * @param thingsRequest
     * @return
     */
    void doHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse);
}
