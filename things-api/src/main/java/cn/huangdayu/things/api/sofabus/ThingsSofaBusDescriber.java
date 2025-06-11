package cn.huangdayu.things.api.sofabus;

import cn.huangdayu.things.common.dsl.ThingsDslInfo;

/**
 * @author huangdayu
 */
public interface ThingsSofaBusDescriber {

    /**
     * 支持的物模型列表
     *
     * @return
     */
    ThingsDslInfo getDSL();

}
