package cn.huangdayu.things.api.message;


import cn.huangdayu.things.common.wrapper.ThingsServlet;

/**
 * @author huangdayu
 */
public interface ThingsInterceptor {

    boolean doIntercept(ThingsServlet thingsServlet);

}
