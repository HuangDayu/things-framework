package cn.huangdayu.things.api.container;

import cn.huangdayu.things.common.dsl.DslInfo;

/**
 * 物模型DSL文档（领域特定语言）引擎
 *
 * @author huangdayu
 */
public interface ThingsDescriber {

    /**
     * 支持的物模型列表
     *
     * @return
     */
    DslInfo getDsl();

}
