package cn.huangdayu.things.common.dsl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author huangdayu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomainSubscribeInfo implements Serializable {


    private String productCode;
    private String eventIdentifier;
    private String eventType;

}
