package cn.huangdayu.things.engine.wrapper;

import cn.huangdayu.things.engine.annotation.ThingsFiltering;
import cn.huangdayu.things.engine.chaining.filters.ThingsFilter;
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
