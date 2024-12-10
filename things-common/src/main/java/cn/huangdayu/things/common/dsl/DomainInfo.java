package cn.huangdayu.things.common.dsl;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author huangdayu
 */
@Data
public class DomainInfo implements Serializable {

    private DomainProfile profile;
    private Set<DomainSubscribeInfo> subscribes;
    private Set<DomainConsumeInfo> consumes;

}
