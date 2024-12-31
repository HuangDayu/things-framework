package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.common.annotation.ThingsPropertyEntity;
import cn.huangdayu.things.api.container.ThingsContainer;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@Data
public class ThingsProperty {

    private ThingsContainer thingsContainer;

    private ThingsPropertyEntity thingsPropertyEntity;

    private Object bean;
}
