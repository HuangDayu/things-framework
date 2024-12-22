package cn.huangdayu.things.common.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThingsAsync {

    private String asyncId;
    private Long timeout;
    private boolean completed;
    private ThingsRequest thingsRequest;
    private ThingsResponse thingsResponse;


}
