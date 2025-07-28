package cn.huangdayu.things.common.dsl.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author huangdayu
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceInfo implements Serializable {

    private String deviceCode;
    private String deviceName;
    private String deviceLocation;
    private String deviceType;
    private String deviceStatus;
    private String deviceDescription;
    private String productCode;
    private String productName;
    private List<DeviceGroup> deviceGroups;
    private List<DeviceTag> deviceTags;


    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        DeviceInfo that = (DeviceInfo) object;
        return Objects.equals(deviceCode, that.deviceCode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(deviceCode);
    }
}
