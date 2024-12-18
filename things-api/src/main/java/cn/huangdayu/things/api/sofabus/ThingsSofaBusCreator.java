package cn.huangdayu.things.api.sofabus;

import cn.huangdayu.things.common.enums.ThingsComponentType;
import cn.huangdayu.things.common.properties.ThingsComponentProperties;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsSofaBusCreator {

    Set<ThingsComponentType> supports();


    ThingsSofaBus create(ThingsComponentProperties property);

}
