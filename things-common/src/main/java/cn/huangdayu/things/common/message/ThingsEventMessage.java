package cn.huangdayu.things.common.message;

import java.io.Serializable;

/**
 * @author huangdayu
 */
public interface ThingsEventMessage extends Serializable {

    String getDeviceCode();

}
