package cn.huangdayu.things.common.wrapper;

import cn.huangdayu.things.common.message.JsonThingsMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author huangdayu
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThingsResponse {

    private Object source;
    private String type;
    private String endpoint;
    private String clientCode;
    private String groupCode;
    private String sessionCode;
    private JsonThingsMessage jtm;
    private Consumer<ThingsResponse> consumer;
    private CompletableFuture<ThingsResponse> future;
}
