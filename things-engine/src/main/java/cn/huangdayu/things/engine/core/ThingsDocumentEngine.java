package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.common.dto.ThingsInfo;

import java.util.Set;

/**
 * 物模型文档引擎
 *
 * @author huangdayu
 */
public interface ThingsDocumentEngine {

    /**
     * 支持的物模型列表
     *
     * @return
     */
    Set<ThingsInfo> getThings();

}
