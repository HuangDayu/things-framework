package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.api.message.ThingsFiltering;
import cn.huangdayu.things.common.annotation.ThingsFilter;
import cn.huangdayu.things.common.enums.ThingsStreamingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThingsFilters {

    private ThingsFilter thingsFilter;
    private ThingsFiltering thingsFiltering;
    private ThingsStreamingType sourceType;
}
