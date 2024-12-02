package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.common.annotation.ThingsProperty;
import cn.huangdayu.things.api.container.ThingsContainer;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@Data
public class ThingsPropertyWrapper {

    private ThingsContainer thingsContainer;

    private ThingsProperty thingsProperty;

    private Object bean;
}
