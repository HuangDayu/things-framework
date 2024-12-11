package cn.huangdayu.things.api.endpoint;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.EndpointCreatorType;
import cn.huangdayu.things.common.enums.EndpointGetterType;
import cn.huangdayu.things.common.exception.ThingsException;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.common.properties.ThingsFrameworkProperties;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.BAD_REQUEST;
import static cn.huangdayu.things.common.constants.ThingsConstants.ErrorCodes.ERROR;
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

    @PostConstruct
    private void init() {
        thingsMessageSenderMap.forEach((key, sender) -> SENDER_MAP.put(sender.type(), sender));
        typeThingsEndpointGetterMap.forEach((key, getter) -> GETTER_MAP.put(getter.type(), getter));
    }

    public ThingsEndpoint create(JsonThingsMessage jtm) {
        return create(jtm, false);
    }

    public ThingsEndpoint create(JsonThingsMessage jtm, boolean reactor) {
        String endpointUri = findEndpointUri(jtm);
        if (StrUtil.isBlank(endpointUri) || thingsFrameworkProperties.getInstance().getEndpointUri().equals(endpointUri)) {
            throw new ThingsException(jtm, BAD_REQUEST, "Not found the target endpointUri.");
        }
        return create(endpointUri, reactor);
    }

    /**
     * 查找目标端点
     * @param jtm
     * @return
     */
    private String findEndpointUri(JsonThingsMessage jtm) {
        return findFirst(true,
                // 如果endpointUri非空且不是自己的端点
                v -> StrUtil.isNotBlank(v) && !thingsFrameworkProperties.getInstance().getEndpointUri().equals(v),
                // 如果是指定目标，则直接发送到目标服务
                () -> GETTER_MAP.get(EndpointGetterType.TARGET).getEndpointUri(jtm),
                // 如果是监听事件，则直接发送到上游服务
                () -> GETTER_MAP.get(EndpointGetterType.EVENT_UPSTREAM).getEndpointUri(jtm),
                // 如果有会话，则发送到相应会话服务
                () -> GETTER_MAP.get(EndpointGetterType.SESSION).getEndpointUri(jtm),
                // 如果存在服务发现的消费或者提供信息中，则发送到相应服务
                () -> GETTER_MAP.get(EndpointGetterType.SERVICE_PROVIDE).getEndpointUri(jtm),
                // 否则发送到上游服务
                () -> GETTER_MAP.get(EndpointGetterType.UPSTREAM).getEndpointUri(jtm));
    }

    public ThingsEndpoint create(String endpointUri) {
        return create(endpointUri, false);
    }

    public ThingsEndpoint create(String endpointUri, boolean reactor) {
        if (StrUtil.isBlank(endpointUri)) {
            throw new ThingsException(BAD_REQUEST, "Things endpoint uri is null.");
        }
        String[] split = endpointUri.split(SCHEMA);
        ThingsEndpointCreator thingsEndpointCreator = SENDER_MAP.get(EndpointCreatorType.valueOf(split[0].toUpperCase()));
        if (null == thingsEndpointCreator) {
            throw new ThingsException(ERROR, "Things endpoint creator is null.");
        }
        return thingsEndpointCreator.create(split[1], reactor);
    }

}
