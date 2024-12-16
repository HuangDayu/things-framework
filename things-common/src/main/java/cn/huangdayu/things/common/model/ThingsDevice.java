package cn.huangdayu.things.common.model;

import com.alibaba.fastjson2.JSONObject;

import java.util.Set;

/**
 * 面向物模型编程： 可获取物模型设备对象，然后进行面向对象编程
 * @author huangdayu
 */
public abstract class ThingsDevice {

    public abstract ThingsService getService(String identifier);

    public abstract Set<ThingsService> getServices();

    public abstract ThingsProperties getThingsProperties();

    public abstract <T> T getProperty(String identifier);

    public abstract void invokeService(String identifier, JSONObject payload);

    public abstract void updateProperty(String identifier, Object value);

    public abstract void unsubscribeEvent(String identifier);

    public abstract void subscribeEvent(String identifier, ThingsEventCallback callback);
}
