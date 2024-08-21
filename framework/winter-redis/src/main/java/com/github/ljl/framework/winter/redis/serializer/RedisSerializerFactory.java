package com.github.ljl.framework.winter.redis.serializer;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-16 10:21
 **/

public abstract class RedisSerializerFactory<V> {
    public static <V> RedisSerializer getSerializer(Class<V> vType) {
        if (vType == String.class) {
            return StringRedisSerializer.get();
        }
        throw new IllegalStateException("unSupport redisType:" + vType.getName());
    }
}
