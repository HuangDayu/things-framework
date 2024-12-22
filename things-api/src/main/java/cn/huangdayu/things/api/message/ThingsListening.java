package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;

/**
 * @author huangdayu
 */
public interface ThingsListening {

    void listen(ThingsRequest thingsRequest, ThingsResponse thingsResponse);

}
