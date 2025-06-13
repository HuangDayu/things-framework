package cn.huangdayu.things.common.events;

import cn.huangdayu.things.common.observer.ThingsBaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThingsPropertiesUpdateEvent implements ThingsBaseEvent {

    private Object source;
    private String productCode;
    private String deviceCode;
    private Object thingsProperty;

}
