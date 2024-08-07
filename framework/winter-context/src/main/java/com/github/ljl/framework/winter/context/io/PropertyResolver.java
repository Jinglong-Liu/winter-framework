package com.github.ljl.framework.winter.context.io;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 10:57
 **/

public interface PropertyResolver {
    boolean containsProperty(String key);

    String getProperty(String key);

    String getProperty(String key, String defaultValue);

    <T> T getProperty(String key, Class<T> targetType);

    <T> T getProperty(String key, Class<T> targetType, T defaultValue);

    String getRequiredProperty(String key);

    <T> T getRequiredProperty(String key, Class<T> targetType);
}
