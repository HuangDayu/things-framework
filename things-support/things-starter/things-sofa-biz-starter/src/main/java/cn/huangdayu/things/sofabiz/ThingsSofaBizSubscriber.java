package cn.huangdayu.things.sofabiz;

import cn.huangdayu.things.api.message.ThingsChaining;
import cn.huangdayu.things.api.message.ThingsSubscriber;
import cn.huangdayu.things.common.wrapper.ThingsRequest;
import cn.huangdayu.things.common.wrapper.ThingsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * @author huangdayu
 */
@Component
@RequiredArgsConstructor
public class ThingsSofaBizSubscriber implements ThingsSubscriber {

    private final ThingsChaining thingsChaining;

    @Override
    public boolean input(ThingsRequest thingsRequest, ThingsResponse thingsResponse) {
        return thingsChaining.input(thingsRequest, thingsResponse);
    }

//    private final ApplicationContext applicationContext;
//    private volatile static ClassLoader currentBizClassloader;
//    private ClassLoader getCurrentBizClassloader() {
//        if (currentBizClassloader == null) {
//            Optional<Map.Entry<String, Object>> first = applicationContext.getBeansWithAnnotation(SpringBootApplication.class).entrySet().stream().findFirst();
//            first.ifPresent(stringObjectEntry -> currentBizClassloader = stringObjectEntry.getValue().getClass().getClassLoader());
//            if (currentBizClassloader == null) {
//                currentBizClassloader = applicationContext.getClassLoader();
//            }
//        }
//        return currentBizClassloader;
//    }
}
