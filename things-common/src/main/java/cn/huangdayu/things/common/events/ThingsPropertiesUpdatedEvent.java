package cn.huangdayu.things.common.events;

import cn.huangdayu.things.common.observer.ThingsBaseEvent;
import cn.huangdayu.things.common.properties.ThingsEngineProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThingsPropertiesUpdatedEvent implements ThingsBaseEvent {

    private Object source;

    private ThingsEngineProperties oldProperties;

    private ThingsEngineProperties newProperties;

}
