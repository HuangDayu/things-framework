package cn.huangdayu.things.common.dsl;

/**
 * @author huangdayu
 */

import lombok.Data;

import java.io.Serializable;

@Data
public class DomainProfile implements Serializable {

    private String schema;

    private DomainProfileInfo tenant;

    private DomainProfileInfo application;

}
