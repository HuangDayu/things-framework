package cn.huangdayu.things.camel.converter;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.message.ThingsRequestMessage;
import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.TypeConverter;
import org.apache.camel.support.TypeConverterSupport;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsTypeConverter extends AbstractTypeConverter {
    @Override
    public Class<?> toType() {
        return byte[].class;
    }

    @Override
    public Class<?> fromType() {
        return ThingsRequestMessage.class;
    }

    @Override
    public TypeConverter typeConverter() {
        return new TypeConverterSupport() {
            @Override
            public <T> T convertTo(Class<T> type, Exchange exchange, Object value) throws TypeConversionException {
                return (T) value.toString().getBytes();
            }
        };
    }
}
