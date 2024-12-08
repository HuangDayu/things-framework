package cn.huangdayu.things.api.endpoint;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.EndpointCreatorType;
import cn.huangdayu.things.common.enums.EndpointGetterType;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.common.observer.event.ThingsInstancesSyncingEvent;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.BAD_REQUEST;
import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.ERROR;
import static cn.huangdayu.things.common.constants.ThingsConstants.Methods.EVENT_LISTENER_START_WITH;
import static cn.huangdayu.things.common.utils.ThingsUtils.findFirst;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsEndpointFactory {


    public static final String RESTFUL_SCHEMA = "restful://";
    public static final String SCHEMA = "://";
    private final ThingsFrameworkProperties thingsFrameworkProperties;
    private final static Map<EndpointCreatorType, ThingsEndpointCreator> SENDER_MAP = new ConcurrentHashMap<>();
    private final static Map<EndpointGetterType, ThingsEndpointGetter> GETTER_MAP = new ConcurrentHashMap<>();
    private final Map<String, ThingsEndpointCreator> thingsMessageSenderMap;
    private final Map<String, ThingsEndpointGetter> typeThingsEndpointGetterMap;
    private final ThingsEventObserver thingsEventObserver;

    @PostConstruct
    private void init() {
        thingsMessageSenderMap.forEach((key, sender) -> SENDER_MAP.put(sender.type(), sender));
        typeThingsEndpointGetterMap.forEach((key, getter) -> GETTER_MAP.put(getter.type(), getter));
    }

    public ThingsEndpoint create(JsonThingsMessage jsonThingsMessage) {
        return create(jsonThingsMessage, false);
    }

    public ThingsEndpoint create(JsonThingsMessage jsonThingsMessage, boolean reactor) {
        String endpointUri = findFirst(true,
                () -> jsonThingsMessage.getMethod().startsWith(EVENT_LISTENER_START_WITH) ? thingsFrameworkProperties.getInstance().getUpstreamUri() : null,
                () -> GETTER_MAP.get(EndpointGetterType.SESSION).getEndpointUri(jsonThingsMessage),
                () -> GETTER_MAP.get(EndpointGetterType.DISCOVERY).getEndpointUri(jsonThingsMessage),
                () -> thingsFrameworkProperties.getInstance().getUpstreamUri());
        if (StrUtil.isBlank(endpointUri)) {
            thingsEventObserver.notifyObservers(new ThingsInstancesSyncingEvent(this));
            throw new ThingsException(jsonThingsMessage, BAD_REQUEST, "Not found the target endpointUri.");
        }
        return create(endpointUri, reactor);
    }

    public ThingsEndpoint create(String endpointUri) {
        return create(endpointUri, false);
    }

    public ThingsEndpoint create(String endpointUri, boolean reactor) {
        if (StrUtil.isBlank(endpointUri)) {
            throw new ThingsException(null, BAD_REQUEST, "Things endpoint uri is null.");
        }
        String[] split = endpointUri.split(SCHEMA);
        ThingsEndpointCreator thingsEndpointCreator = SENDER_MAP.get(EndpointCreatorType.valueOf(split[0].toUpperCase()));
        if (null == thingsEndpointCreator) {
            throw new ThingsException(null, ERROR, "Things endpoint creator is null.");
        }
        return thingsEndpointCreator.create(split[1], reactor);
    }

}
