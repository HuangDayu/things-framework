package cn.huangdayu.things.camel.components;

import cn.huangdayu.things.camel.CamelSofaBusConstructor;
import cn.hutool.core.collection.CollUtil;
import org.apache.camel.spi.PropertyConfigurer;
import org.apache.camel.support.DefaultComponent;

/**
 * @author huangdayu
 */
public abstract class ComponentSofaBus extends AbstractSofaBus {

    protected DefaultComponent component;


    protected abstract DefaultComponent buildComponent();


    public ComponentSofaBus(CamelSofaBusConstructor constructor) {
        super(constructor);
        this.component = buildComponent();
        initConfigure();
        camelContext.addComponent(constructor.getProperties().getName(), component);
    }

    private void initConfigure() {
        if (CollUtil.isNotEmpty(constructor.getProperties().getProperties())) {
            PropertyConfigurer configurer = component.getComponentPropertyConfigurer();
            constructor.getProperties().getProperties().forEach((k, v) -> configurer.configure(camelContext, component, k, v, true));
        }
    }

    @Override
    public boolean start() {
        if (component.isStarted()) {
            return true;
        }
        component.start();
        return true;
    }

    @Override
    public boolean stop() {
        if (component.isStopped()) {
            return true;
        }
        component.stop();
        return true;
    }

    @Override
    public boolean isStarted() {
        return component.isStarted();
    }

}
