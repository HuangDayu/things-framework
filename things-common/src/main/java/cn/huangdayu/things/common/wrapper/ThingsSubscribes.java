package cn.huangdayu.things.common.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author huangdayu
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThingsSubscribes implements Serializable {

    private String productCode;
    private String deviceCode;
    private String methodType;
    private String identifier;


}
