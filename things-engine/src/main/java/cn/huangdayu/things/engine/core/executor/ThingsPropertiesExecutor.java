package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.annotation.ThingsPropertyEntity;
import cn.huangdayu.things.engine.core.ThingsProperties;
import cn.huangdayu.things.engine.wrapper.ThingsProperty;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;

import static cn.huangdayu.things.engine.core.executor.ThingsRegisterExecutor.DEVICE_PROPERTY_MAP;
import static cn.huangdayu.things.engine.core.executor.ThingsRegisterExecutor.PRODUCT_PROPERTY_MAP;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsPropertiesExecutor implements ThingsProperties {


    @Override
    public <T> T getPropertyEntity(String productCode) {
        ThingsProperty thingsProperty = PRODUCT_PROPERTY_MAP.get(productCode);
        if (thingsProperty != null) {
            ThingsPropertyEntity thingsPropertyEntity = thingsProperty.getThingsPropertyEntity();
            if (thingsPropertyEntity.productPublic()) {
                return (T) thingsProperty.getBean();
            }
        }
        return null;
    }

    @Override
    public <T> T getPropertyEntity(String productCode, String deviceCode) {
        ThingsProperty thingsPropertyForDevice = DEVICE_PROPERTY_MAP.get(deviceCode, productCode);
        if (thingsPropertyForDevice == null) {
            ThingsProperty thingsPropertyForProduct = PRODUCT_PROPERTY_MAP.get(productCode);
            if (thingsPropertyForProduct == null) {
                return null;
            }
            if (thingsPropertyForProduct.getThingsPropertyEntity().productPublic()) {
                return (T) thingsPropertyForProduct.getBean();
            }
            thingsPropertyForDevice = new ThingsProperty(thingsPropertyForProduct.getThingsContainer(), thingsPropertyForProduct.getThingsPropertyEntity(), ObjectUtil.clone(thingsPropertyForProduct.getBean()));
            DEVICE_PROPERTY_MAP.put(deviceCode, productCode, thingsPropertyForDevice);
        }
        return (T) thingsPropertyForDevice.getBean();
    }


    @Override
    public <T> T getProperty(String productCode, String deviceCode, String propertyName) {
        Object bean = getPropertyEntity(productCode, deviceCode);
        if (bean != null) {
            return (T) ReflectUtil.getFieldValue(bean, propertyName);
        }
        return null;
    }

    @Override
    public void setProperty(String productCode, String deviceCode, String propertyName, Object value) {
        Object bean = getPropertyEntity(productCode, deviceCode);
        if (bean != null) {
            ReflectUtil.setFieldValue(bean, propertyName, value);
            postProperties(productCode, deviceCode, bean);
        }
    }

    @Override
    public void updatePropertyEntity(String productCode, String deviceCode, Object properties) {
        postProperties(productCode, deviceCode, properties);
    }

    @Override
    public void postProperty(String productCode, String deviceCode) {

    }


    private void postProperties(String productCode, String deviceCode, Object properties) {
        // TODO 上报属性（不使用AOP或者动态字节码的形式监听对象字段的额更新，而是使用主动调用或者定时上报的形式）
    }
}
