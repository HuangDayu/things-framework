package cn.huangdayu.things.api.message;

import cn.huangdayu.things.common.message.JsonThingsMessage;

/**
 * @author huangdayu
 */
public interface ThingsConverting {


    JsonThingsMessage input(byte[] bytes);


    byte[] output(JsonThingsMessage jtm);

}
