package cn.huangdayu.things.engine.message;

import java.io.Serializable;

/**
 * @author huangdayu
 */
public interface ThingsEventMessage extends Serializable {

    String getDeviceCode();

}
