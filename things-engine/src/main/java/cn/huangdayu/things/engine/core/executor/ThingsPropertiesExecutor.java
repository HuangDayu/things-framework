package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.annotation.ThingsPropertyEntity;
import cn.huangdayu.things.common.events.ThingsPropertiesUpdateEvent;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.engine.core.ThingsProperties;
import cn.huangdayu.things.engine.wrapper.ThingsProperty;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.RequiredArgsConstructor;


/**
 * @author huangdayu
 */
@ThingsBean
@RequiredArgsConstructor
public class ThingsPropertiesExecutor implements ThingsProperties {

    private final ThingsContainerManager thingsContainerManager;
    private final ThingsEventObserver thingsEventObserver;

    @Override
    public <T> T getPropertyEntity(String productCode) {
        ThingsProperty thingsProperty = thingsContainerManager.getThingsPropertyMap().get(productCode);
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
        ThingsProperty thingsPropertyForDevice = thingsContainerManager.getDevicePropertyMap().get(deviceCode, productCode);
        if (thingsPropertyForDevice == null) {
            ThingsProperty thingsPropertyForProduct = thingsContainerManager.getThingsPropertyMap().get(productCode);
            if (thingsPropertyForProduct == null) {
                return null;
            }
            if (thingsPropertyForProduct.getThingsPropertyEntity().productPublic()) {
                return (T) thingsPropertyForProduct.getBean();
            }
            thingsPropertyForDevice = new ThingsProperty(thingsPropertyForProduct.getThingsContainer(), thingsPropertyForProduct.getThingsPropertyEntity(), ObjectUtil.clone(thingsPropertyForProduct.getBean()));
            thingsContainerManager.getDevicePropertyMap().put(deviceCode, productCode, thingsPropertyForDevice);
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
        ThingsProperty thingsPropertyForDevice = thingsContainerManager.getDevicePropertyMap().get(deviceCode, productCode);
        Object bean = thingsPropertyForDevice.getBean();
        if (bean != null) {
            ReflectUtil.setFieldValue(bean, propertyName, value);
            postProperties(productCode, deviceCode, thingsPropertyForDevice);
        }
    }

    @Override
    public void updatePropertyEntity(String productCode, String deviceCode, Object properties) {
        ThingsProperty thingsPropertyForDevice = thingsContainerManager.getDevicePropertyMap().get(deviceCode, productCode);
        if (thingsPropertyForDevice == null) {
            ThingsProperty thingsPropertyForProduct = thingsContainerManager.getThingsPropertyMap().get(productCode);
            if (thingsPropertyForProduct == null) {
                return;
            }
            if (thingsPropertyForProduct.getThingsPropertyEntity().productPublic()) {
                thingsPropertyForProduct.setBean(properties);
                thingsContainerManager.getThingsPropertyMap().put(productCode, thingsPropertyForProduct);
                postProperties(productCode, deviceCode, thingsPropertyForDevice);
                return;
            }
            thingsPropertyForDevice = ObjectUtil.clone(thingsPropertyForProduct);
        }
        thingsPropertyForDevice.setBean(properties);
        thingsContainerManager.getDevicePropertyMap().put(deviceCode, productCode, thingsPropertyForDevice);
        postProperties(productCode, deviceCode, thingsPropertyForDevice);
    }

    private void postProperties(String productCode, String deviceCode, ThingsProperty thingsProperty) {
        thingsEventObserver.notifyObservers(new ThingsPropertiesUpdateEvent(this, productCode, deviceCode, thingsProperty));
    }
}
