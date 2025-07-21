package cn.huangdayu.things.common.dsl;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class ThingsActionInfo implements Serializable {
    /**
     * 说明：行为标识符；类型：String；是否必选：有条件必选；长度：{1,100}；取值：唯一ID；备注：行为唯一标识；关键字：identifier,id,Identifier
     */
    private String identifier;
    /**
     * 说明：英文名称；类型：String；是否必选：有条件必选；长度：{1,50}；取值：英文字符；备注：行为英文标识；关键字：name,ServiceName,Servicename,Name
     */
    private String name;

    /**
     * 说明：中文名称；类型：String；是否必选：有条件必选；长度：{1,50}；取值：中文字符；备注：行为中文标识；关键字：name,ServiceName,Servicename,Name
     */
    private String cnName;

    /**
     * 说明：显示标题；类型：String；是否必选：否；长度：{1,100}；取值：显示文本；备注：用户界面显示用；关键字：displayName,displayName
     */
    private String displayName;

    /**
     * 说明：是否必选；类型：Bool；是否必选：否；取值：True/False；备注：是否必须实现；关键字：required,tmRequired,Required
     */
    private Boolean required;

    /**
     * 说明：调用方式；类型：Enum；是否必选：否；取值：SYNC/ASYNC；备注：同步/异步执行；关键字：callType,ActionType,CallType
     */
    private String callType;

    /**
     * 说明：行为描述；类型：String；是否必选：否；长度：{1,50}；取值：描述文本；备注：功能说明；关键字：description,desc,Description
     */
    private String description;

    /**
     * 说明：输入参数；类型：Array；是否必选：是；长度：[1,100]；取值：参数列表；备注：执行所需参数；关键字：input,inputParams,inputs,Input,inputData
     */
    private Set<ThingsParamInfo> input;
    /**
     * 说明：输出参数；类型：Array；是否必选：否；长度：[1,100]；取值：参数列表；备注：执行结果数据；关键字：output,outputParams,outputs,Output,outputData
     */
    private Set<ThingsParamInfo> output;

    /**
     * 说明：是否自定义；类型：Bool；是否必选：否；取值：True/False；备注：用户扩展功能；关键字：custom,Custom
     */
    private Boolean custom;
}