package cn.huangdayu.things.client;

import lombok.Data;

/**
 * @author huangdayu
 */
@Data
public class ThingsClientProperties {

    private String instanceId;

    private String gatewayUri;

    private String gatewayToken;

}
