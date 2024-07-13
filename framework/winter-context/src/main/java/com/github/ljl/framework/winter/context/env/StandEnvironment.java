package com.github.ljl.framework.winter.context.env;

import com.github.ljl.framework.winter.context.context.ApplicationContext;
import com.github.ljl.framework.winter.context.io.PropertyResolver;

import java.util.List;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 11:43
 **/

public class StandEnvironment implements Environment {
    private ApplicationContext applicationContext;

    private PropertyResolver propertyResolver;
    public StandEnvironment(ApplicationContext applicationContext, PropertyResolver propertyResolver) {
        this.applicationContext = applicationContext;
        this.propertyResolver = propertyResolver;
    }

    @Override
    public boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    @Override
    public <T> T getBean(String name) {
        return applicationContext.getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    @Override
    public <T> List<T> getBeans(Class<T> requiredType) {
        return applicationContext.getBeans(requiredType);
    }

    @Override
    public void close() {
        applicationContext.close();
    }

    @Override
    public boolean containsProperty(String key) {
        return propertyResolver.containsProperty(key);
    }

    @Override
    public String getProperty(String name) {
        return propertyResolver.getProperty(name);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return getProperty(key, defaultValue);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        return propertyResolver.getProperty(key, targetType);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return propertyResolver.getProperty(key, targetType, defaultValue);
    }

    @Override
    public String getRequiredProperty(String key) {
        return propertyResolver.getRequiredProperty(key);
    }

    @Override
    public <T> T getRequiredProperty(String key, Class<T> targetType) {
        return propertyResolver.getRequiredProperty(key, targetType);
    }
}
