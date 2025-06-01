package cn.huangdayu.things.api.sofabus;

import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import cn.huangdayu.things.common.properties.ThingsSofaBusProperties;

import java.util.Set;

/**
 * @author huangdayu
 */
public interface ThingsSofaBusCreator {

    Set<ThingsSofaBusType> supports();


    ThingsSofaBus create(ThingsSofaBusProperties property, ThingsSofaBusInputting thingsChaining);

}
