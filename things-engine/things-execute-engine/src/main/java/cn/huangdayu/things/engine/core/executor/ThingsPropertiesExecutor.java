package cn.huangdayu.things.engine.core.executor;

import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.annotation.ThingsPropertyEntity;
import cn.huangdayu.things.common.events.ThingsPropertiesUpdateEvent;
import cn.huangdayu.things.common.observer.ThingsEventObserver;
import cn.huangdayu.things.engine.core.ThingsProperties;
import cn.huangdayu.things.engine.wrapper.ThingsPropertyEntities;
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
        ThingsPropertyEntities thingsPropertyEntities = thingsContainerManager.getThingsPropertyMap().get(productCode);
        if (thingsPropertyEntities != null) {
            ThingsPropertyEntity thingsPropertyEntity = thingsPropertyEntities.getThingsPropertyEntity();
            if (thingsPropertyEntity.productPublic()) {
                return (T) thingsPropertyEntities.getBean();
            }
        }
        return null;
    }

    @Override
    public <T> T getPropertyEntity(String productCode, String deviceCode) {
        ThingsPropertyEntities thingsPropertyEntitiesForDevice = thingsContainerManager.getDevicePropertyMap().get(deviceCode, productCode);
        if (thingsPropertyEntitiesForDevice == null) {
            ThingsPropertyEntities thingsPropertyEntitiesForProduct = thingsContainerManager.getThingsPropertyMap().get(productCode);
            if (thingsPropertyEntitiesForProduct == null) {
                return null;
            }
            if (thingsPropertyEntitiesForProduct.getThingsPropertyEntity().productPublic()) {
                return (T) thingsPropertyEntitiesForProduct.getBean();
            }
            thingsPropertyEntitiesForDevice = new ThingsPropertyEntities(thingsPropertyEntitiesForProduct.getThingsContainer(), thingsPropertyEntitiesForProduct.getThingsPropertyEntity(), ObjectUtil.clone(thingsPropertyEntitiesForProduct.getBean()));
            thingsContainerManager.getDevicePropertyMap().put(deviceCode, productCode, thingsPropertyEntitiesForDevice);
        }
        return (T) thingsPropertyEntitiesForDevice.getBean();
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
        ThingsPropertyEntities thingsPropertyEntitiesForDevice = thingsContainerManager.getDevicePropertyMap().get(deviceCode, productCode);
        Object bean = thingsPropertyEntitiesForDevice.getBean();
        if (bean != null) {
            ReflectUtil.setFieldValue(bean, propertyName, value);
            postProperties(productCode, deviceCode, thingsPropertyEntitiesForDevice);
        }
    }

    @Override
    public void updatePropertyEntity(String productCode, String deviceCode, Object properties) {
        ThingsPropertyEntities thingsPropertyEntitiesForDevice = thingsContainerManager.getDevicePropertyMap().get(deviceCode, productCode);
        if (thingsPropertyEntitiesForDevice == null) {
            ThingsPropertyEntities thingsPropertyEntitiesForProduct = thingsContainerManager.getThingsPropertyMap().get(productCode);
            if (thingsPropertyEntitiesForProduct == null) {
                return;
            }
            if (thingsPropertyEntitiesForProduct.getThingsPropertyEntity().productPublic()) {
                thingsPropertyEntitiesForProduct.setBean(properties);
                thingsContainerManager.getThingsPropertyMap().put(productCode, thingsPropertyEntitiesForProduct);
                postProperties(productCode, deviceCode, thingsPropertyEntitiesForDevice);
                return;
            }
            thingsPropertyEntitiesForDevice = ObjectUtil.clone(thingsPropertyEntitiesForProduct);
        }
        thingsPropertyEntitiesForDevice.setBean(properties);
        thingsContainerManager.getDevicePropertyMap().put(deviceCode, productCode, thingsPropertyEntitiesForDevice);
        postProperties(productCode, deviceCode, thingsPropertyEntitiesForDevice);
    }

    private void postProperties(String productCode, String deviceCode, ThingsPropertyEntities thingsPropertyEntities) {
        thingsEventObserver.notifyObservers(new ThingsPropertiesUpdateEvent(this, productCode, deviceCode, thingsPropertyEntities));
    }
}
