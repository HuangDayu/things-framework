package cn.huangdayu.things.api.endpoint;

/**
 * @author huangdayu
 */
public interface ThingsEndpointFactory {

     <S> S create(Class<S> endpointType, String endpointUri);

}
