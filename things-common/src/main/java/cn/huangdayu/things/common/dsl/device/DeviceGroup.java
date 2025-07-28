package cn.huangdayu.things.common.dsl.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceGroup implements Serializable {

    private String groupCode;
    private String groupName;

}
