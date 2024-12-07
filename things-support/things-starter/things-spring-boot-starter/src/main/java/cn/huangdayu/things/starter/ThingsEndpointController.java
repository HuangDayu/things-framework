package cn.huangdayu.things.starter;

import cn.huangdayu.things.api.endpoint.ThingsEndpoint;
import cn.huangdayu.things.common.dto.ThingsInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsInstance;
import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@ConditionalOnBean(ThingsEndpoint.class)
@RequiredArgsConstructor
@RestController
@RequestMapping
public class ThingsEndpointController {

    private final ThingsEndpoint thingsEndpoint;

    @PostMapping("/things/message")
    public JsonThingsMessage handleMessage(@RequestBody JsonThingsMessage message) {
        log.debug("ThingsEndpoint handleMessage: {}", message);
        return thingsEndpoint.handleMessage(message);
    }

    @PostMapping("/things/event")
    public void handleEvent(@RequestBody JsonThingsMessage message) {
        log.debug("ThingsEndpoint handleEvent: {}", message);
        thingsEndpoint.handleEvent(message);
    }

    /**
     * 异步消息发送
     *
     * @param message
     * @return
     */
    @PostMapping("/things/message/reactor")
    public Mono<JsonThingsMessage> reactorMessage(@RequestBody JsonThingsMessage message) {
        log.debug("ThingsEndpoint reactorMessage: {}", message);
        return thingsEndpoint.reactorMessage(message);
    }

    @GetMapping("/things/dsl")
    public Set<ThingsInfo> getThingsDsl() {
        return thingsEndpoint.getThingsDsl();
    }

    @PostMapping("/things/exchange")
    public ThingsInstance exchange(@RequestBody ThingsInstance thingsInstance) {
        log.debug("ThingsEndpoint exchange: {}", JSON.toJSONString(thingsInstance));
        return thingsEndpoint.exchange(thingsInstance);
    }


}
