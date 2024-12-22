package cn.huangdayu.things.api.message;


import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;

/**
 * @author huangdayu
 */
public interface ThingsIntercepting {

    /**
     * 在请求处理之前被调用。
     * @param thingsRequest
     * @param thingsResponse
     * @return
     */
    default boolean preHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        return true;
    }

    /**
     * 在请求处理完成但未返回结果之前被调用
     * @param thingsRequest
     * @param thingsResponse
     */
    default void postHandle(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
    }

    /**
     * 在请求完全处理完成之后被调用。
     * @param thingsRequest
     * @param thingsResponse
     */
    default void afterCompletion(ThingsRequest thingsRequest, ThingsResponse thingsResponse, Exception exception) {
    }

}
