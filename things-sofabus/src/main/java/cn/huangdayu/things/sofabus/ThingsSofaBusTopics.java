package cn.huangdayu.things.sofabus;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.enums.ThingsSofaBusType;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@ThingsBean
public class ThingsSofaBusTopics {

    private static final Map<ThingsSofaBusType, String> TOPICS = new ConcurrentHashMap<>();

    /**
     * 获取请求需要发送的topicCode列表，包括【系统级，租户级，应用级，产品级，设备级，分组级，任务级】
     * @param thingsRequest
     * @return
     */
    public Set<String> getPublishTopicCodes(ThingsRequest thingsRequest) {


        return Set.of();
    }

    public Set<String> getSubscribeTopicCodes() {
        return Set.of();
    }

}
