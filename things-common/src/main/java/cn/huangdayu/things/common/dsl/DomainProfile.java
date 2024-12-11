package cn.huangdayu.things.common.dsl;

/**
 * @author huangdayu
 */

import cn.huangdayu.things.common.wrapper.ThingsInstance;
import lombok.Data;

import java.io.Serializable;

@Data
public class DomainProfile implements Serializable {

    private String schema;

    private DomainProfileInfo tenant;

    private DomainProfileInfo application;

}
