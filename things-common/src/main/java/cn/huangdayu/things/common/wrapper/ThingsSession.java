package cn.huangdayu.things.common.wrapper;

import lombok.Data;

/**
 * @author huangdayu
 */
@Data
public class ThingsSession {

    private String sessionCode;

    private boolean online;

    private Long onlineTime;

    private String productCode;

    private String deviceCode;

}
