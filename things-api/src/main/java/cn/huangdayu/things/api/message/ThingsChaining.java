package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;

/**
 * @author huangdayu
 */
public interface ThingsChaining {

    void input(ThingsRequest thingsRequest, ThingsResponse thingsResponse);

    void output(ThingsRequest thingsRequest, ThingsResponse thingsResponse);
}
