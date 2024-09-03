package cn.huangdayu.things.engine.chaining.interceptor;

import cn.huangdayu.things.engine.wrapper.ThingsServlet;

/**
 * @author huangdayu
 */
public interface Interceptor {

    boolean doIntercept(ThingsServlet thingsServlet);

}
