package cn.huangdayu.things.common.model;

import cn.huangdayu.things.common.message.ThingsEventMessage;

/**
 * 面向物模型编程： 可获取物模型设备对象，然后进行面向对象编程
 * @author huangdayu
 */
public abstract class ThingsServer {

    public abstract void publishEvent(ThingsEventMessage thingsEventMessage);

}
