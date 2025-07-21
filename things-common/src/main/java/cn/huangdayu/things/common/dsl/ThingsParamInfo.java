package cn.huangdayu.things.common.dsl;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThingsParamInfo implements Serializable {
    /**
     * 标识符 String 有条件 必选 {1,100} N/A id,Identifier,identifier 参数的标识
     */
    private String identifier;

    /**
     * 名称 String 有条件 必选 {1,50} N/A displayName,Name,name 参数的名称
     */
    private String name;

    /**
     * 属性中文名称（条件下必选），长度[1,50]，可作为唯一标识，关键字：name,displayName,propertyName,Name
     */
    private String cnName;

    /**
     * 说明：显示标题；类型：String；是否必选：否；长度：[1,50]；取值：显示文本；备注：用户界面显示用；关键字：displayName,DisplayName
     */
    private String displayName;

    /**
     * 描述 String 否 {1,50} N/A description,desc,Description 输入输出参数的描述信息
     */
    private String description;

    /**
     * 是否必选属性（非必选），取值：True/False，关键字：required,Mandatory,tmRequired,Required
     */
    private Boolean required;


    /**
     * 读写标识（非必选），取值：READ/WRITE/READWRITE，关键字：rwMode,accessMode,rwACL,rwFlag,mode,RwMode
     */
    private String accessMode;


    /**
     * 方向 String 否  PARAM_INPUT：输入参数。PARAM_OUTPUT： 输出参数 direction,Direction
     */
    private String direction;

    /**
     * 序号 Integer 否 [1,100] paraOrder,ParaOrder
     */
    private Integer paraOrder;

    /**
     * 基本数据类型（必选），取值：int/float/double/string/bitMap/array/struct，关键字：dataType,type,DataType
     */
    private String dataType;

    /**
     * 最大值（非必选），仅数值型有效，关键字：max,maximum,Max
     */
    private String max;

    /**
     * 最小值（非必选），仅数值型有效，关键字：min,minimum,Min
     */
    private String min;

    /**
     * 变化幅度（非必选），仅数值型有效，关键字：step,scale,Step
     */
    private String step;

    /**
     * 数据单位（非必选），关键字：unit,Unit
     */
    private String unit;

    /**
     * 单位名称（非必选），关键字：unitName
     */
    private String unitName;

    /**
     * 默认值（非必选），关键字：default,defaultValue,Default
     */
    private String defaultValue;

    /**
     * 精度（必选），仅float/double有效，关键字：precise,Precise
     */
    private String precise;

    /**
     * 字符串最小长度（非必选），仅string有效，关键字：minLength,min,MinLength
     */
    private String minLength;

    /**
     * 字符串最大长度（非必选），仅string有效，关键字：maxLength,max,MaxLength
     */
    private String maxLength;


    /**
     * 数据长度（非必选），仅TEXT有效（≤2048字节），关键字：length,dataLength,Length
     * 可支持数据类型：DATE、TEXT
     */
    private Integer dataLength;


    /**
     * 时间格式（非必选），仅DATE有效，关键字：timestamp,Timestamp
     * 可支持数据类型：DATE、TEXT
     */
    private String timestamp;


    /**
     * 是否自定义（非必选），取值：True/False，关键字：custom,Custom
     */
    private Boolean custom;

    /**
     * 数组长度（非必选），关键字：size,Size
     * 可支持数据类型：ARRAY
     */
    private Integer size;

    /**
     * 子元素类型（非必选），取值：STRUCT/INT/FLOAT/DOUBLE/TEXT，关键字：childDataType
     * 可支持数据类型：ARRAY
     */
    private String childDataType;

    /**
     * 数组项说明（非必选），关键字：arrayDesc,ArrayDesc
     * 可支持数据类型：ARRAY
     */
    private String arrayDesc;

    /**
     * 枚举项说明（非必选），关键字：value,enumDesc,mapping value,Value
     * 可支持数据类型：ENUM，BOOL
     */
    private String enumDesc;

    /**
     * 枚举值 String/enum/Integer 否 枚举值 value,enumValue,mappingValue,Value
     * 可支持数据类型：ENUM，BOOL
     */
    private String enumValue;

    /**
     * 结构体项说明（非必选），关键字：structDesc
     * 仅dataType为Struct时有效。结构体说明，由2个或者2个以上属性参数组合而成，用来表示多个
     * 属性相互紧密关联，同时存在才有意义的数据类型，例如颜色RGB，HSV等。
     * 格式为：属性参数（2..N）。属性参数可以对应于不同的数据类型，但不能是结构体类型。
     * 结构体类型数据不支持嵌套。
     * 可支持数据类型：STRUCT
     */
    private String structDesc;

    /**
     * 位图项说明（非必选），关键字：bitmapDesc
     * 图项说明，每位表示真、假两种状态变量格式：
     * 0（False）:<状态描述>
     * 1（True）:<状态描述>
     */
    private String bitmapDesc;
}