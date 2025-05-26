package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;

/**
 * @author huangdayu
 */
public interface ThingsChaining {

    boolean input(ThingsRequest thingsRequest, ThingsResponse thingsResponse);

    boolean output(ThingsRequest thingsRequest, ThingsResponse thingsResponse);
}
