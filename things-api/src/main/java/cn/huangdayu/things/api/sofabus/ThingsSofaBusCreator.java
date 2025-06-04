package cn.huangdayu.things.api.sofabus;

import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import cn.huangdayu.things.common.properties.ThingsEngineProperties;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsSofaBusCreator {

    Set<ThingsSofaBusType> supports();


    ThingsSofaBus create(ThingsEngineProperties.ThingsSofaBusProperties property, ThingsSofaBusInputting thingsChaining);

}
