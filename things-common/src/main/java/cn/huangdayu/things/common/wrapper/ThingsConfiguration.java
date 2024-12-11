package cn.huangdayu.things.common.wrapper;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author huangdayu
 */
@Data
public class ThingsConfiguration implements Serializable {

    private String instanceCode;
    private String upstreamUri;
    private Set<ThingsPlugin> plugins;

}
