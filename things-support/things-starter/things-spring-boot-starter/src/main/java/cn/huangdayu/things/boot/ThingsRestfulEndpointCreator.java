package cn.huangdayu.things.boot;

import cn.huangdayu.things.api.endpoint.ThingsEndpoint;
import cn.huangdayu.things.api.endpoint.ThingsEndpointCreator;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.EndpointCreatorType;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.lang.func.Func0;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestTemplateAdapter;
import org.springframework.web.service.invoker.HttpExchangeAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.util.function.Function;

import static cn.huangdayu.things.common.enums.EndpointCreatorType.RESTFUL;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsRestfulEndpointCreator implements ThingsEndpointCreator {


    /**
     * server vs class vs bean
     */
    private static final TimedCache<String, Object> CLIENT_CACHE = CacheUtil.newTimedCache(60 * 1000 * 5);

    private static <S> S createRestClient(Class<S> serviceType, String server) {
        return clientCache(serviceType, server, baseUrl -> {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));
            SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
            simpleClientHttpRequestFactory.setConnectTimeout(2000);
            simpleClientHttpRequestFactory.setReadTimeout(5000);
            restTemplate.setRequestFactory(simpleClientHttpRequestFactory);
            restTemplate.setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(ClientHttpResponse response) throws IOException {
                    return true;
                }

                @Override
                public void handleError(ClientHttpResponse response) throws IOException {

                }
            });
            RestTemplateAdapter restTemplateAdapter = RestTemplateAdapter.create(restTemplate);
            return createClient(serviceType, restTemplateAdapter);
        });
    }

    private static <S> S clientCache(Class<S> serviceType, String server, Function<String, S> function) {
        return (S) CLIENT_CACHE.get(server.concat(serviceType.getName()),
                (Func0<Object>) () -> function.apply(!server.startsWith("http") && !server.startsWith("https") ? "http://" + server : server));
    }

    private static <S> S createClient(Class<S> serviceType, HttpExchangeAdapter httpExchangeAdapter) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder().exchangeAdapter(httpExchangeAdapter).build();
        return factory.createClient(serviceType);
    }

    @Override
    public EndpointCreatorType type() {
        return RESTFUL;
    }

    @Override
    public ThingsEndpoint create(String endpointUri) {
        return createRestClient(ThingsRestfulEndpoint.class, endpointUri);
    }
}
