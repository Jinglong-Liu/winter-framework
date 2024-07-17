package com.github.ljl.framework.winter.redis.serializer;

import com.github.ljl.framework.winter.redis.exception.SerializationException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-16 10:07
 **/

public class StringRedisSerializer implements RedisSerializer<String> {

    /**
     * TODO: charset
     */
    private static final Charset charset = StandardCharsets.UTF_8;

    private StringRedisSerializer() {}
    private static StringRedisSerializer instance = null;

    public static StringRedisSerializer get() {
        if (Objects.isNull(instance)) {
            synchronized (StringRedisSerializer.class) {
                if (Objects.isNull(instance)) {
                    instance = new StringRedisSerializer();
                }
            }
        }
        return instance;
    }
    @Override
    public byte[] serialize(String string) throws SerializationException {
        return (string == null ? null : string.getBytes(charset));
    }

    @Override
    public String deserialize(byte[] bytes) throws SerializationException {
        return (bytes == null ? null : new String(bytes, charset));
    }
}
