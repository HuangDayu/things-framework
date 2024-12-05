package cn.huangdayu.things.boot;

import cn.huangdayu.things.api.endpoint.ThingsEndpoint;
import cn.huangdayu.things.common.dto.ThingsInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@ConditionalOnBean(ThingsEndpoint.class)
@RequiredArgsConstructor
@RestController
@RequestMapping
public class ThingsEndpointController {

    private final ThingsEndpoint thingsEndpoint;

    @PostMapping("/things/send")
    public JsonThingsMessage send(@RequestBody JsonThingsMessage message) {
        return thingsEndpoint.send(message);
    }

    @PostMapping("/things/publish")
    public void publish(@RequestBody JsonThingsMessage message) {
        thingsEndpoint.publish(message);
    }

    @GetMapping("/things/dsl")
    public Set<ThingsInfo> getThingsDsl() {
        return thingsEndpoint.getThingsDsl();
    }

    @PostMapping("/things/exchange")
    public ThingsInstance exchange(@RequestBody ThingsInstance thingsInstance) {
        return thingsEndpoint.exchange(thingsInstance);
    }


}
