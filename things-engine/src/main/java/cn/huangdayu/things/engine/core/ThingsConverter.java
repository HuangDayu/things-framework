package cn.huangdayu.things.engine.core;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import cn.huangdayu.things.engine.wrapper.ThingsFunction;

/**
 * @author huangdayu
 */
public interface ThingsConverter {


    /**
     * 参数转换器
     *
     * @param jsonThingsMessage
     * @param thingsFunction
     * @return
     */
    Object[] args(JsonThingsMessage jsonThingsMessage, ThingsFunction thingsFunction);
}
