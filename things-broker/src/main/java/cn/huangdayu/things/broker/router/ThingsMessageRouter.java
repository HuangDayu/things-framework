/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.huangdayu.things.broker.router;

import cn.huangdayu.things.engine.async.ThreadPoolFactory;
import cn.huangdayu.things.common.constants.ThingsConstants;
import cn.huangdayu.things.common.message.JsonThingsMessage;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 适配适配器上行消息分发器
 * <p>
 * mqtt topic 规则  /things/{productCode}/{deviceCode}/{method}
 * amqp topic 规则  广播： things-bus  点对点： things-{productCode}
 *
 * @author huangdayu
 */
@Component
@RequiredArgsConstructor
public class ThingsMessageRouter extends RouteBuilder {

    private final Environment env;
    private final ThreadPoolExecutor threadPoolExecutor = ThreadPoolFactory.newExecutor(3);

    @Value("${camel.servlet.mapping.context-path:/*}")
    private String contextPath;

    @Override
    public void configure() throws Exception {


        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .enableCORS(true)
                .port(env.getProperty("server.port", "8080"))
                .contextPath(contextPath.substring(0, contextPath.length() - 2));

        rest("/things/message")
                .description("Adapter message service")
                .consumes("application/json")
                .produces("application/json")
                .post().type(JSONObject.class)
                .description("publisher").outType(String.class)
                .responseMessage().code(200).message("OK").endResponseMessage()
                .to("direct:things-message-router");

        from("direct:things-message-router")
                .choice()
                .when()
                .method("thingsMessageFilterFactory", "handler")
                .setBody(constant(new JsonThingsMessage().clientError(ThingsConstants.ErrorCodes.UNAUTHORIZED)))
                .otherwise()
                // 多线程并发处理
                .threads().executorService(threadPoolExecutor)
                .multicast().parallelProcessing()
                // 打印日志
                .log("Received message: ${body}")
                // 规则处理/自定义处理
                .wireTap("bean:thingsMessageHandlerFactory?method=handler")
                // 下游消费
                // .wireTap("kafka:things-bus")
                .setBody(constant(new JsonThingsMessage().success())).end();

    }

}
