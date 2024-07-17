package com.github.ljl.framework.winter.redis.serializer;

import com.github.ljl.framework.winter.redis.exception.SerializationException;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-16 10:06
 **/

public interface RedisSerializer<T> {
    /**
     * Serialize the given object to binary data.
     *
     * @param t object to serialize. Can be {@literal null}.
     * @return the equivalent binary data. Can be {@literal null}.
     */

    byte[] serialize(T t) throws SerializationException;

    /**
     * Deserialize an object from the given binary data.
     *
     * @param bytes object binary representation. Can be {@literal null}.
     * @return the equivalent object instance. Can be {@literal null}.
     */

    T deserialize(byte[] bytes) throws SerializationException;
}
