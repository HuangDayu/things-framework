package cn.huangdayu.things.common.wrapper;

import cn.huangdayu.things.common.message.ThingsRequestMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
public class ThingsRequest extends ThingsServlet {

    public ThingsRequest(ThingsRequestMessage trm) {
        this.trm = trm;
    }

    private ThingsRequestMessage trm;
    private Consumer<ThingsResponse> responseConsumer;
    private CompletableFuture<ThingsResponse> responseFuture;
}
