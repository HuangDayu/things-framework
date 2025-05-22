package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.api.message.ThingsHandling;
import cn.huangdayu.things.common.annotation.ThingsHandler;
import cn.huangdayu.things.common.enums.ThingsChainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThingsHandlers {

    private ThingsHandler thingsHandler;
    private ThingsHandling thingsHandling;
    private ThingsChainingType chainingType;

}
