package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.engine.core.ThingsProperties;
import cn.huangdayu.things.engine.wrapper.ThingsPropertyWrapper;
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
    public <T> T getProperties(String productCode) {
        ThingsPropertyWrapper thingsPropertyWrapper = PRODUCT_PROPERTY_MAP.get(productCode);
        if (thingsPropertyWrapper != null) {
            cn.huangdayu.things.common.annotation.ThingsProperty thingsProperty = thingsPropertyWrapper.getThingsProperty();
            if (thingsProperty.productPublic()) {
                return (T) thingsPropertyWrapper.getBean();
            }
        }
        return null;
    }

    @Override
    public <T> T getProperties(String productCode, String deviceCode) {
        ThingsPropertyWrapper thingsPropertyWrapperForDevice = DEVICE_PROPERTY_MAP.get(deviceCode, productCode);
        if (thingsPropertyWrapperForDevice == null) {
            ThingsPropertyWrapper thingsPropertyWrapperForProduct = PRODUCT_PROPERTY_MAP.get(productCode);
            if (thingsPropertyWrapperForProduct == null) {
                return null;
            }
            if (thingsPropertyWrapperForProduct.getThingsProperty().productPublic()) {
                return (T) thingsPropertyWrapperForProduct.getBean();
            }
            thingsPropertyWrapperForDevice = new ThingsPropertyWrapper(thingsPropertyWrapperForProduct.getThingsContainer(), thingsPropertyWrapperForProduct.getThingsProperty(), ObjectUtil.clone(thingsPropertyWrapperForProduct.getBean()));
            DEVICE_PROPERTY_MAP.put(deviceCode, productCode, thingsPropertyWrapperForDevice);
        }
        return (T) thingsPropertyWrapperForDevice.getBean();
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
