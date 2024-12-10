package cn.huangdayu.things.gateway;

import cn.huangdayu.things.common.dsl.DslInfo;

import java.util.List;

/**
 * @author huangdayu
 */
public interface ThingsDslManager {

    void addDsl(String dsl);

    DslInfo getDsl();


}
