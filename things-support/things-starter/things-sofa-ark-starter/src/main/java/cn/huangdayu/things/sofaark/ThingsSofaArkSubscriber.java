package cn.huangdayu.things.sofaark;

import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.message.ThingsSubscriber;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
public class ThingsSofaArkSubscriber implements ThingsSubscriber {

    private final ThingsChaining thingsChaining;

    @Override
    public boolean input(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        return thingsChaining.input(thingsRequest, thingsResponse);
    }
}
