package cn.huangdayu.things.starter;

import cn.huangdayu.things.starter.endpoint.ThingsEndpoint;
import cn.huangdayu.things.common.dsl.DslInfo;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.wrapper.ThingsConfiguration;
import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@ConditionalOnBean(ThingsEndpoint.class)
@RequiredArgsConstructor
@RestController
@RequestMapping
public class ThingsEndpointController {

    private final ThingsEndpoint thingsEndpoint;

    @PostMapping("/things/message")
    public JsonThingsMessage handleMessage(@RequestBody JsonThingsMessage jtm) {
        log.debug("ThingsEndpoint handleMessage: {}", jtm);
        return thingsEndpoint.handleMessage(jtm);
    }

    @PostMapping("/things/event")
    public void handleEvent(@RequestBody JsonThingsMessage jtm) {
        log.debug("ThingsEndpoint handleEvent: {}", jtm);
        thingsEndpoint.handleEvent(jtm);
    }

    /**
     * 异步消息发送
     *
     * @param jtm
     * @return
     */
    @PostMapping("/things/message/reactor")
    public Mono<JsonThingsMessage> reactorMessage(@RequestBody JsonThingsMessage jtm) {
        log.debug("ThingsEndpoint reactorMessage: {}", jtm);
        return thingsEndpoint.reactorMessage(jtm);
    }

    @GetMapping("/things/dsl")
    public DslInfo getDsl() {
        return thingsEndpoint.getDsl();
    }

    /**
     * 配置实例
     */
    @PostMapping("/things/configuration")
    public void configuration(@RequestBody ThingsConfiguration thingsConfiguration) {
        log.debug("ThingsEndpoint configuration: {}", JSON.toJSONString(thingsConfiguration));
        thingsEndpoint.configuration(thingsConfiguration);
    }
}
