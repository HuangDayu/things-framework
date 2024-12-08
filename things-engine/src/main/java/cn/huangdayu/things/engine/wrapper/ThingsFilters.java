package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.api.message.ThingsFilter;
import cn.huangdayu.things.common.annotation.ThingsFiltering;
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

    private ThingsFiltering thingsFiltering;
    private ThingsFilter thingsFilter;

}
