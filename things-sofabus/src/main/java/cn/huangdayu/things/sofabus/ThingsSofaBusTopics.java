package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsSubscribes;

import java.util.Set;

/**
 * 获取请求需要发送的topicCode列表，包括【系统级，租户级，应用级，产品级，设备级，分组级，任务级】
 * $things/${productCode}/${deviceCode}/${methodType}/${methodAction}/${identifier}/${messageId}
 *
 * 设备级topic
 * 事件上报： $things/5e06bfee334dd4f33759f5b3/661e35467bdccc0126d1a595/event/post/fireWaring
 * 服务调用： $things/5e06bfee334dd4f33759f5b3/661e35467bdccc0126d1a595/service/request/openDoor
 * 服务响应： $things/5e06bfee334dd4f33759f5b3/661e35467bdccc0126d1a595/service/response/openDoor
 * 属性上报： $things/5e06bfee334dd4f33759f5b3/661e35467bdccc0126d1a595/property/post/temperature
 * 属性设置： $things/5e06bfee334dd4f33759f5b3/661e35467bdccc0126d1a595/property/set/temperature
 * 属性查询： $things/5e06bfee334dd4f33759f5b3/661e35467bdccc0126d1a595/property/get/temperature
 *
 * 系统级topic
 * 模型上报： $things/5e06bfee334dd4f33759f5b3/661e35467bdccc0126d1a595/system/post/model
 * 模型查询： $things/5e06bfee334dd4f33759f5b3/661e35467bdccc0126d1a595/system/get/model
 * 配置上报： $things/5e06bfee334dd4f33759f5b3/661e35467bdccc0126d1a595/system/post/config
 * 配置查询： $things/5e06bfee334dd4f33759f5b3/661e35467bdccc0126d1a595/system/get/config
 * 配置设置： $things/5e06bfee334dd4f33759f5b3/661e35467bdccc0126d1a595/system/set/config
 * @author huangdayu
 */
@ThingsBean
public class ThingsSofaBusTopics {


    /**
     * 提供了什么模型服务，就订阅什么主题
     * 消费了什么模型服务，就订阅什么主题
     *
     * @param thingsSubscribes
     * @return
     */
    public String getSubscribeTopic(ThingsSubscribes thingsSubscribes) {
        return null;
    }


    public Set<String> getSubscribeTopics(ThingsRequest thingsRequest) {
        return null;
    }
}
