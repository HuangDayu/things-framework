package cn.huangdayu.things.common.wrapper;

import cn.huangdayu.things.common.message.ThingsResponseMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author huangdayu
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Data
public class ThingsResponse extends ThingsServlet {

    public ThingsResponse(ThingsResponseMessage trm) {
        this.trm = trm;
    }

    private ThingsResponseMessage trm;
    
}
