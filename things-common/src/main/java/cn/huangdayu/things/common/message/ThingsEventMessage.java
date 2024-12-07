package cn.huangdayu.things.common.message;

import java.io.Serializable;

/**
 * @author huangdayu
 */
public interface ThingsEventMessage extends Serializable {

    default String getDeviceCode() {
        return null;
    }

}
