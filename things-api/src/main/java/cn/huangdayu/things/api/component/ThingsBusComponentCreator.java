package cn.huangdayu.things.api.component;

import cn.huangdayu.things.common.enums.ThingsComponentType;
import cn.huangdayu.things.common.properties.ThingsComponentProperties;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsBusComponentCreator {

    Set<ThingsComponentType> supports();


    ThingsBusComponent create(ThingsComponentProperties property);

}
