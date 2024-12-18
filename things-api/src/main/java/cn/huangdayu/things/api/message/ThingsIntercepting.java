package cn.huangdayu.things.api.message;


import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;

/**
 * @author huangdayu
 */
public interface ThingsIntercepting {

    /**
     * 在请求处理之前被调用。
     * @param request
     * @param response
     * @return
     */
    default boolean preHandle(ThingsRequest request, ThingsResponse response, ThingsHandling handling) {
        return true;
    }

    /**
     * 在请求处理完成但未返回结果之前被调用
     * @param request
     * @param response
     */
    default void postHandle(ThingsRequest request, ThingsResponse response, ThingsHandling handling) {
    }

    /**
     * 在请求完全处理完成之后被调用。
     * @param request
     * @param response
     */
    default void afterCompletion(ThingsRequest request, ThingsResponse response, ThingsHandling handling, Exception exception) {
    }

}
