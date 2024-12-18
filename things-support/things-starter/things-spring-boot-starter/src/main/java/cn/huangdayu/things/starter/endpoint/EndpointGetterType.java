package cn.huangdayu.things.starter.endpoint;

import lombok.RequiredArgsConstructor;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
public enum EndpointGetterType {

    TARGET(1),

    EVENT_UPSTREAM(2),

    SESSION(3),

    SERVICE_PROVIDE(4),

    UPSTREAM(5),

    ;

    private final int index;


}
