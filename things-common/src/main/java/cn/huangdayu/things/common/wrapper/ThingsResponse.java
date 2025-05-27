package cn.huangdayu.things.common.wrapper;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author huangdayu
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Data
public class ThingsResponse extends ThingsServlet {

    public ThingsResponse(JsonThingsMessage jtm) {
        super(jtm);
    }
}
