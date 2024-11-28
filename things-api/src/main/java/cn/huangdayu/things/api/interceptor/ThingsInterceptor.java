package cn.huangdayu.things.api.interceptor;


import cn.huangdayu.things.common.wrapper.ThingsServlet;

/**
 * @author huangdayu
 */
public interface ThingsInterceptor {

    boolean doIntercept(ThingsServlet thingsServlet);

}
