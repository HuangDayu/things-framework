package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.api.container.ThingsContainer;
import cn.huangdayu.things.common.annotation.ThingsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThingsEntities {

    private ThingsContainer thingsContainer;
    private String productCode;
    private Object bean;
    private ThingsEntity thingsEntity;
}
