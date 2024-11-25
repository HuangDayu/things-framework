package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.common.dto.ThingsInfo;

import java.util.Set;

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
    Set<ThingsInfo> getThingsDsl();

}