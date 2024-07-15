package com.github.ljl.framework.winter.context.utils;

import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 09:21
 **/

public class DataConvertUtils {
    private static Map<Class<?>, Function<String, Object>> converters = new HashMap<>();
    public static Map<Class<?>, Function<String, Object>> converter() {
        return converters;
    }
    public static <T> T convertToType(String value, Class<T> type) throws IllegalArgumentException {
        if (converters.containsKey(type)) {
            Function<String, Object> function = converters.get(type);
            if (Objects.nonNull(function)) {
                return (T) function.apply(value);
            }
        }
        for (Class<?> clz: converters.keySet()) {
            if (type.isAssignableFrom(clz)) {
                Function<String, Object> function = converters.get(clz);
                return (T) function.apply(value);
            }
        }
        throw new IllegalArgumentException("illegal clazz type, fail to convert: " + type.getName());
    }
    static {
        converters.put(String.class, s -> s);
        converters.put(boolean.class, s -> Boolean.parseBoolean(s));
        converters.put(Boolean.class, s -> Boolean.valueOf(s));

        converters.put(byte.class, s -> Byte.parseByte(s));
        converters.put(Byte.class, s -> Byte.valueOf(s));

        converters.put(short.class, s -> Short.parseShort(s));
        converters.put(Short.class, s -> Short.valueOf(s));

        converters.put(int.class, s -> Integer.parseInt(s));
        converters.put(Integer.class, s -> Integer.valueOf(s));

        converters.put(long.class, s -> Long.parseLong(s));
        converters.put(Long.class, s -> Long.valueOf(s));

        converters.put(float.class, s -> Float.parseFloat(s));
        converters.put(Float.class, s -> Float.valueOf(s));

        converters.put(double.class, s -> Double.parseDouble(s));
        converters.put(Double.class, s -> Double.valueOf(s));

        converters.put(LocalDate.class, s -> LocalDate.parse(s));
        converters.put(LocalTime.class, s -> LocalTime.parse(s));
        converters.put(LocalDateTime.class, s -> LocalDateTime.parse(s));
        converters.put(ZonedDateTime.class, s -> ZonedDateTime.parse(s));
        converters.put(Duration.class, s -> Duration.parse(s));
        converters.put(ZoneId.class, s -> ZoneId.of(s));
    }
}
