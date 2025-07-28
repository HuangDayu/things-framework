package cn.huangdayu.things.common.model;

import com.alibaba.fastjson2.JSONObject;

/**
 * @author huangdayu
 */
public abstract class ThingsAction {

    public abstract void invoke(JSONObject params);

}
