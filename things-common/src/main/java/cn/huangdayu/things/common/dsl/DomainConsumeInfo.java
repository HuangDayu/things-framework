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
public class DomainConsumeInfo implements Serializable {


    private String productCode;
    private String serviceIdentifier;

}
