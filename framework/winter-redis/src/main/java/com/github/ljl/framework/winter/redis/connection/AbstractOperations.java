package com.github.ljl.framework.winter.redis.connection;

import com.github.ljl.framework.winter.redis.serializer.RedisSerializer;
import com.github.ljl.framework.winter.redis.serializer.RedisSerializerFactory;
import com.github.ljl.framework.winter.redis.template.RedisTemplate;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-16 10:14
 **/

abstract class AbstractOperations<K, V> {

    protected Class<?> kType;
    protected Class<?> vType;

    protected final RedisTemplate<K, V> template;

    public AbstractOperations(RedisTemplate<K, V> redisTemplate, Class<?> kType, Class<?> vType) {
        this.template = redisTemplate;
        this.kType = kType;
        this.vType = vType;
    }

    abstract class ValueDeserializingRedisCallback implements RedisCallback<V> {
        private Object key;

        public ValueDeserializingRedisCallback(Object key) {
            this.key = key;
        }

        public final V doInRedis(RedisConnection connection) {
            byte[] result = inRedis(rawKey(key), connection);
            return deserializeValue(result);
        }

        protected abstract byte[] inRedis(byte[] rawKey, RedisConnection connection);
    }
    V deserializeValue(byte[] value) {
        if (valueSerializer() == null) {
            return (V) value;
        }
        return (V) valueSerializer().deserialize(value);
    }


    RedisSerializer keySerializer() {
        return template.getKeySerializer();
    }

    protected byte[] rawKey(Object key) {

        if (keySerializer() == null && key instanceof byte[]) {
            return (byte[]) key;
        }

        return keySerializer().serialize(key);
    }

    protected RedisSerializer stringSerializer() {
        return RedisSerializerFactory.getSerializer(String.class);
    }

    @SuppressWarnings("unchecked")
    protected byte[] rawString(String key) {
        return stringSerializer().serialize(key);
    }

    @SuppressWarnings("unchecked")
    protected byte[] rawValue(Object value) {
        if (valueSerializer() == null && value instanceof byte[]) {
            return (byte[]) value;
        }

        return valueSerializer().serialize(value);
    }

    RedisSerializer valueSerializer() {
        return RedisSerializerFactory.getSerializer(vType);
    }
}
