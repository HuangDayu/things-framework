package cn.huangdayu.things.api.instances;

import cn.huangdayu.things.common.dsl.DslInfo;

/**
 * @author huangdayu
 */
public interface ThingsInstancesDslManager {

    void addAllDsl(DslInfo dslInfo);

    DslInfo getDsl();


}
