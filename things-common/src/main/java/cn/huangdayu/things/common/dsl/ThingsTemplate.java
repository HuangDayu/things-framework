package cn.huangdayu.things.common.dsl;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Data
public class ThingsTemplate implements Serializable {
    /**
     * 说明：物模板标识符；类型：String；是否必选：否；长度：[1,100]；取值：唯一ID；备注：模板唯一标识；关键字：id,Id,identifier
     */
    private String identifier;

    /**
     * 说明：物模板名称；类型：String；是否必选：是；长度：[1,50]；取值：模板名称；备注：可包含行业信息；关键字：name,Name
     */
    private String name;

    /**
     * 说明：中文名称；类型：String；是否必选：有条件必选；长度：{1,50}；取值：中文字符；备注：组件中文标识；关键字：name,Name
     */
    private String cnName;

    /**
     * 说明：显示标题；类型：String；是否必选：否；长度：[1,50]；取值：显示文本；备注：用户界面显示用；关键字：displayName,DisplayName
     */
    private String displayName;

    /**
     * 说明：模板描述；类型：String；是否必选：否；长度：[1,200]；取值：描述文本；备注：功能说明；关键字：description,Description
     */
    private String description;


    /**
     * Status 状态信息 Object 否 设备在线/离线、激活/未激活等状态。
     */
    private ThingsStatusInfo status;

    /**
     * Profile 档案信息 Object 否 设备身份详情的静态描述，包含设备身份标识和设备描述信息。
     */
    private ThingsProfileInfo profile;

    /**
     * 说明：行为列表；类型：List；是否必选：否；长度：0..N；取值：行为对象列表；备注：模板包含的行为；关键字：ActionList,Actions,actions
     */
    private Set<ThingsActionInfo> actions;
    /**
     * 说明：属性列表；类型：List；是否必选：否；长度：0..N；取值：属性对象列表；备注：模板包含的属性；关键字：properties
     */
    private Set<ThingsParamInfo> properties;

    /**
     * 说明：事件列表；类型：List；是否必选：否；长度：0..N；取值：事件对象列表；备注：模板包含的事件；关键字：eventList,events
     */
    private Set<ThingsEventInfo> events;

    /**
     * 说明：组件列表；类型：List；是否必选：否；长度：0..N；取值：组件对象列表；备注：模板包含的组件；关键字：FunctionBlock,FunctionBlocks
     */
    private Set<ThingsComponent> components;

    /**
     * 说明：数据模式；类型：JsonObject；是否必选：否；取值：复杂结构定义；备注：校验模板信息；关键字：schema,Schema,dataSchema,DataSchema,tmData
     */
    private JSONObject schema;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ThingsTemplate that = (ThingsTemplate) o;
        return Objects.equals(profile, that.profile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profile);
    }
}