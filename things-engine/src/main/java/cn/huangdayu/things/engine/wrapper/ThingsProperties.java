package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.engine.annotation.ThingsProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@Data
public class ThingsProperties {

    private ThingsContainer thingsContainer;

    private ThingsProperty thingsProperty;

    private Object bean;
}
