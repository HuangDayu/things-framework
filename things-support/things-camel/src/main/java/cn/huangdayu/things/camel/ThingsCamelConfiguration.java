package cn.huangdayu.things.camel;

import cn.huangdayu.things.camel.converter.AbstractTypeConverter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author huangdayu
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ThingsCamelConfiguration {

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
