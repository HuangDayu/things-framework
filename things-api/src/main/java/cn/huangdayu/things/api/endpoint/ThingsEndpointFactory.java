package cn.huangdayu.things.api.endpoint;

import cn.huangdayu.things.common.enums.EndpointProtocolType;

/**
 * 端点工厂，解决端点客户端创建和复用的问题
 *
 * @author huangdayu
 */
public interface ThingsEndpointFactory {

    EndpointProtocolType endpointProtocol();

    <S> S create(Class<S> endpointType, String endpointUri);

}
