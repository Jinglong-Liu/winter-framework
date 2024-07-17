package com.github.ljl.framework.winter.context.io;

import com.github.ljl.framework.winter.context.utils.DataConvertUtils;

import java.time.*;
import java.util.*;
import java.util.function.Function;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 10:58
 **/

public class StandardPropertyResolver implements PropertyResolver {
    private Map<String, String> properties = new HashMap<>();
    private Map<Class<?>, Function<String, Object>> converters = DataConvertUtils.converter();

    public StandardPropertyResolver(Properties props) {
        this.properties.putAll(System.getenv());
        Set<String> names = props.stringPropertyNames();
        for (String name : names) {
            this.properties.put(name, props.getProperty(name));
        }
    }

    @Override
    public boolean containsProperty(String key) {
        return this.properties.containsKey(key);
    }

    @Override
    public String getProperty(String key) {
        PropertyExpr keyExpr = parsePropertyExpr(key);
        if (keyExpr != null) {
            if (keyExpr.getDefaultValue() != null) {
                return getProperty(keyExpr.getKey(), keyExpr.getDefaultValue());
            } else {
                return getProperty(keyExpr.getKey());
            }
        }
        String value = this.properties.get(key);
        if (value != null) {
            return parseValue(value);
        }
        return null;
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value == null ? parseValue(defaultValue) : value;
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        String value = getProperty(key);
        if (value == null) {
            // 另一种情况，key为常量, @Value("100")
            return convert(targetType, key);
        }
        return convert(targetType, value);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return convert(targetType, value);
    }

    @Override
    public String getRequiredProperty(String key) {
        String value = getProperty(key);
        return Objects.requireNonNull(value, "Property '" + key + "' not found.");
    }

    @Override
    public <T> T getRequiredProperty(String key, Class<T> targetType) {
        T value = getProperty(key, targetType);
        return Objects.requireNonNull(value, "Property '" + key + "' not found.");
    }

    @SuppressWarnings("unchecked")
    private <T> T convert(Class<?> clazz, String value) {
        Function<String, Object> fn = this.converters.get(clazz);
        if (fn == null) {
            throw new IllegalArgumentException("Unsupported value type: " + clazz.getName());
        }
        return (T) fn.apply(value);
    }

    private String parseValue(String value) {
        PropertyExpr expr = parsePropertyExpr(value);
        if (expr == null) {
            return value;
        }
        if (expr.getDefaultValue() != null) {
            return getProperty(expr.getKey(), expr.getDefaultValue());
        } else {
            return getRequiredProperty(expr.getKey());
        }
    }

    private PropertyExpr parsePropertyExpr(String key) {
        if (key.startsWith("${") && key.endsWith("}")) {
            int n = key.indexOf(':');
            if (n == (-1)) {
                // no default value: ${key}
                String k = notEmpty(key.substring(2, key.length() - 1));
                return new PropertyExpr(k, null);
            } else {
                // has default value: ${key:default}
                String k = notEmpty(key.substring(2, n));
                return new PropertyExpr(k, key.substring(n + 1, key.length() - 1));
            }
        }
        return null;
    }
    private String notEmpty(String key) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Invalid key: " + key);
        }
        return key;
    }
}
