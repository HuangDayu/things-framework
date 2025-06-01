package cn.huangdayu.things.common.events;

import cn.huangdayu.things.common.observer.ThingsBaseEvent;
import cn.huangdayu.things.common.properties.ThingsSystemProperties;
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

    private ThingsSystemProperties oldProperties;

    private ThingsSystemProperties newProperties;

}
