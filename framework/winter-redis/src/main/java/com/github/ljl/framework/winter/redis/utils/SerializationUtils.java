package com.github.ljl.framework.winter.redis.utils;

import com.github.ljl.framework.winter.redis.serializer.RedisSerializer;

import java.util.*;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-16 20:13
 **/

public class SerializationUtils {
    public static <T extends Collection<?>> T deserializeValues(Collection<byte[]> rawValues, Class<T> type,
                                                         RedisSerializer<?> redisSerializer) {
        // connection in pipeline/multi mode
        if (rawValues == null) {
            if (type.isAssignableFrom(Set.class)) {
                return (T) Collections.emptySet();
            } else if(type.isAssignableFrom(List.class)) {
                return (T) Collections.EMPTY_LIST;
            } else if(type.isAssignableFrom(Map.class)) {
                return (T) Collections.EMPTY_MAP;
            }
            return null;
        }

        Collection<Object> values = (List.class.isAssignableFrom(type) ? new ArrayList<>(rawValues.size())
                : new LinkedHashSet<>(rawValues.size()));
        for (byte[] bs : rawValues) {
            values.add(redisSerializer.deserialize(bs));
        }

        return (T) values;
    }
//    public static <T> List<T> deserialize(List<byte[]> rawValues,
//                                          RedisSerializer<T> redisSerializer) {
//        return deserializeValues(rawValues, List.class, redisSerializer);
//    }
}
