package cn.huangdayu.things.common.dsl.template;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class ThingsEventInfo implements Serializable {

    /**
     * 事件标识符（有条件必选），长度{1,100}，可作为唯一标识，关键字：identifier,id,Identifier
     */
    private String identifier;

    /**
     * 事件英文名称（有条件必选），长度{1,50}，可作为唯一标识，关键字：name,eventName,Name
     */
    private String name;

    /**
     * 事件中文名称（有条件必选），长度{1,50}，可作为唯一标识，关键字：name,eventName,Name
     */
    private String cnName;

    /**
     * 说明：显示标题；类型：String；是否必选：否；长度：[1,50]；取值：显示文本；备注：用户界面显示用；关键字：displayName,DisplayName
     */
    private String displayName;

    /**
     * 事件描述（非必选），长度{1,50}，关键字：description,desc,Description
     */
    private String description;

    /**
     * 是否必选事件（非必选），取值：True/False，关键字：required,Required
     */
    private Boolean required;

    /**
     * 事件类型（非必选），取值：INFO_EVENT_TYPE/ALERT_EVENT_TYPE/ERROR_EVENT_TYPE，关键字：eventType,EventType
     */
    private String eventType;

    /**
     * 输出参数（非必选），长度[1,100]，关键字：output,outputParams,outpouts,Output,outputdata,outputData
     */
    private Set<ThingsParamInfo> output;

    /**
     * 是否自定义功能（非必选），取值：True/False，关键字：custom,Custom
     */
    private Boolean custom;
}