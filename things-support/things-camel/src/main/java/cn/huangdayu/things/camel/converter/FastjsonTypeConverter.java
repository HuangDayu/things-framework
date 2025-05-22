package cn.huangdayu.things.camel.converter;

import com.alibaba.fastjson2.JSONObject;
import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.TypeConverter;
import org.apache.camel.support.TypeConverterSupport;
import org.springframework.stereotype.Component;

/**
 * @author huangdayu
 */
@Component
public class FastjsonTypeConverter extends AbstractTypeConverter {
    @Override
    public Class<?> toType() {
        return byte[].class;
    }

    @Override
    public Class<?> fromType() {
        return JSONObject.class;
    }

    @Override
    public TypeConverter typeConverter() {
        return new TypeConverterSupport() {
            @Override
            public <T> T convertTo(Class<T> type, Exchange exchange, Object value) throws TypeConversionException {
                return (T) ((JSONObject) value).toJSONString().getBytes();
            }
        };
    }
}
