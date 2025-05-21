package cn.huangdayu.things.starter;

import cn.huangdayu.things.starter.endpoint.ThingsEndpoint;
import cn.huangdayu.things.starter.endpoint.ThingsEndpointCreator;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.starter.enums.EndpointCreatorType;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestTemplateAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpExchangeAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;

import static cn.huangdayu.things.starter.enums.EndpointCreatorType.RESTFUL;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsRestfulEndpointCreator implements ThingsEndpointCreator {


    /**
     * server vs class vs bean
     */
    private static final TimedCache<String, Object> CLIENT_CACHE = CacheUtil.newTimedCache(60 * 1000 * 5);

    private static final String RESTFUL_PREFIX = "restful://";
    private static final String REST_PREFIX = "rest://";
    private static final String HTTPS_PREFIX = "https://";
    private static final String HTTP_PREFIX = "http://";


    @Override
    public EndpointCreatorType type() {
        return RESTFUL;
    }

    @Override
    public ThingsEndpoint create(String endpointUri) {
        return createClient(ThingsRestfulEndpoint.class, endpointUri, false);
    }

    @Override
    public ThingsEndpoint create(String endpointUri, boolean reactor) {
        return createClient(ThingsRestfulEndpoint.class, endpointUri, reactor);
    }

    private static <S> S createClient(Class<S> serviceType, String server, boolean reactor) {
        String baseUrl = getBaseUrl(server);
        String clientName = baseUrl.concat("_").concat(serviceType.getName()).concat("_").concat(String.valueOf(reactor));
        return (S) CLIENT_CACHE.get(clientName, () -> reactor ? createWebFluxClient(serviceType, baseUrl) : createRestClient(serviceType, baseUrl));
    }

    private static <S> S createRestClient(Class<S> serviceType, String server) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(server));
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(2000);
        simpleClientHttpRequestFactory.setReadTimeout(5000);
        restTemplate.setRequestFactory(simpleClientHttpRequestFactory);
        restTemplate.setErrorHandler(response -> true);
        RestTemplateAdapter restTemplateAdapter = RestTemplateAdapter.create(restTemplate);
        return createProxyClient(serviceType, restTemplateAdapter);
    }

    private static <S> S createWebFluxClient(Class<S> serviceType, String server) {
        WebClient webClient = WebClient.builder().baseUrl(server).build();
        WebClientAdapter webClientAdapter = WebClientAdapter.create(webClient);
        return createProxyClient(serviceType, webClientAdapter);
    }

    private static <S> S createProxyClient(Class<S> serviceType, HttpExchangeAdapter httpExchangeAdapter) {
        return HttpServiceProxyFactory.builder().exchangeAdapter(httpExchangeAdapter).build().createClient(serviceType);
    }

    private static String getBaseUrl(String server) {
        if (server.startsWith(RESTFUL_PREFIX)) {
            return server.replace(RESTFUL_PREFIX, HTTP_PREFIX);
        }
        if (server.startsWith(REST_PREFIX)) {
            return server.replace(REST_PREFIX, HTTP_PREFIX);
        }
        if (server.startsWith(HTTPS_PREFIX) || server.startsWith(HTTP_PREFIX)) {
            return server;
        }
        return HTTP_PREFIX + server;
    }
}
