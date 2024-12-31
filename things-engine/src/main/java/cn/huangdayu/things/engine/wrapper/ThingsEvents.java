package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.common.annotation.ThingsEventEntity;
import cn.huangdayu.things.api.container.ThingsContainer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThingsEvents {

    private ThingsContainer thingsContainer;
    private ThingsEventEntity thingsEventEntity;
    private Object bean;

}
