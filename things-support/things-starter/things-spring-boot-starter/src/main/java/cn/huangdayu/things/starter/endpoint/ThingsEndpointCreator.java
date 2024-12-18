package cn.huangdayu.things.starter.endpoint;

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
