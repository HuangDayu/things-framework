package cn.huangdayu.things.api.endpoint;

import cn.huangdayu.things.common.enums.EndpointCreatorType;

/**
 * 端点工厂，解决端点客户端创建和复用的问题
 *
 * @author huangdayu
 */
public interface ThingsEndpointCreator {

    EndpointCreatorType type();

    ThingsEndpoint create(String endpointUri);

    ThingsEndpoint create(String endpointUri, boolean reactor);
}
