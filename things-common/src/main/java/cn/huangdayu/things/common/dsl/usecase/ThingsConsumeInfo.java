package cn.huangdayu.things.common.dsl.usecase;

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
public class ThingsConsumeInfo implements Serializable {


    private String productCode;
    private String serviceIdentifier;

}
