package cn.huangdayu.things.gateway.manager;

import cn.huangdayu.things.gateway.converter.AbstractTypeConverter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author huangdayu
 */
@RequiredArgsConstructor
@Component
public class ConverterManager {

    private final CamelContext camelContext;
    private final Map<String, AbstractTypeConverter> typeConverterMap;

    @PostConstruct
    public void init() {
        if (typeConverterMap != null) {
            for (Map.Entry<String, AbstractTypeConverter> entry : typeConverterMap.entrySet()) {
                AbstractTypeConverter converter = entry.getValue();
                camelContext.getTypeConverterRegistry().addTypeConverter(converter.toType(), converter.fromType(), converter.typeConverter());
            }
        }
    }

}
