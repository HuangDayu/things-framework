package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.huangdayu.things.common.annotation.ThingsProperty;
import cn.huangdayu.things.engine.core.ThingsPropertiesEngine;
import cn.huangdayu.things.engine.wrapper.ThingsProperties;

import static cn.huangdayu.things.engine.core.executor.ThingsContainerExecutor.DEVICE_PROPERTY_MAP;
import static cn.huangdayu.things.engine.core.executor.ThingsContainerExecutor.PRODUCT_PROPERTY_MAP;

/**
 * @author huangdayu
 */
@ThingsBean
public class ThingsPropertiesExecutor implements ThingsPropertiesEngine {


    @Override
    public <T> T getProperties(String productCode) {
        ThingsProperties thingsProperties = PRODUCT_PROPERTY_MAP.get(productCode);
        if (thingsProperties != null) {
            ThingsProperty thingsProperty = thingsProperties.getThingsProperty();
            if (thingsProperty.productPublic()) {
                return (T) thingsProperties.getBean();
            }
        }
        return null;
    }

    @Override
    public <T> T getProperties(String productCode, String deviceCode) {
        ThingsProperties thingsPropertiesForDevice = DEVICE_PROPERTY_MAP.get(deviceCode, productCode);
        if (thingsPropertiesForDevice == null) {
            ThingsProperties thingsPropertiesForProduct = PRODUCT_PROPERTY_MAP.get(productCode);
            if (thingsPropertiesForProduct == null) {
                return null;
            }
            if (thingsPropertiesForProduct.getThingsProperty().productPublic()) {
                return (T) thingsPropertiesForProduct.getBean();
            }
            thingsPropertiesForDevice = new ThingsProperties(thingsPropertiesForProduct.getThingsContainer(), thingsPropertiesForProduct.getThingsProperty(), ObjectUtil.clone(thingsPropertiesForProduct.getBean()));
            DEVICE_PROPERTY_MAP.put(deviceCode, productCode, thingsPropertiesForDevice);
        }
        return (T) thingsPropertiesForDevice.getBean();
    }


    @Override
    public <T> T getProperty(String productCode, String deviceCode, String propertyName) {
        Object bean = getProperties(productCode, deviceCode);
        if (bean != null) {
            return (T) ReflectUtil.getFieldValue(bean, propertyName);
        }
        return null;
    }

}
