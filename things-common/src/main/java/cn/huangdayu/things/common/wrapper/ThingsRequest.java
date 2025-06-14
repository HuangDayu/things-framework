package cn.huangdayu.things.common.wrapper;

import cn.huangdayu.things.common.message.JsonThingsMessage;
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

    public ThingsRequest(JsonThingsMessage jtm) {
        super(jtm);
    }

    private Object source;
    private Object target;
    private Object subscriber;
    private Consumer<ThingsResponse> responseConsumer;
    private CompletableFuture<ThingsResponse> responseFuture;
}
