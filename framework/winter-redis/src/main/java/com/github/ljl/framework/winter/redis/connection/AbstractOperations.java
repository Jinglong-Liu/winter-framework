package com.github.ljl.framework.winter.redis.connection;

import com.github.ljl.framework.winter.redis.serializer.RedisSerializer;
import com.github.ljl.framework.winter.redis.serializer.RedisSerializerFactory;
import com.github.ljl.framework.winter.redis.template.RedisTemplate;
import com.github.ljl.framework.winter.redis.utils.SerializationUtils;

import java.util.List;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-16 10:14
 **/

abstract class AbstractOperations<K, V> {

    protected final RedisTemplate<K, V> template;

    public AbstractOperations(RedisTemplate<K, V> redisTemplate) {
        this.template = redisTemplate;
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

    public <T> T execute(RedisCallback<T> callback) {
        return template.execute(callback);
    }


    protected  <T> T deserializeValue(byte[] value) {
        if (valueSerializer() == null) {
            return (T) value;
        }
        return (T) valueSerializer().deserialize(value);
    }


    protected RedisSerializer keySerializer() {
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

    protected RedisSerializer valueSerializer() {
        //return RedisSerializerFactory.getSerializer(vType);
        return template.getValueSerializer();
    }

    protected RedisSerializer hashValueSerializer() {
        return template.getHashValueSerializer();
    }
    protected RedisSerializer hashKeySerializer() {
        return template.getHashKeySerializer();
    }



    @SuppressWarnings("unchecked")
    protected byte[] rawString(String key) {
        return stringSerializer().serialize(key);
    }

    @SuppressWarnings("unchecked")
    protected <VT> byte[] rawValue(VT value) {
        if (valueSerializer() == null && value instanceof byte[]) {
            return (byte[]) value;
        }

        return valueSerializer().serialize(value);
    }



    protected <HK> byte[] rawHashKey(HK hashKey) {
        if (hashKeySerializer() == null && hashKey instanceof byte[]) {
            return (byte[]) hashKey;
        }
        return hashKeySerializer().serialize(hashKey);
    }

    protected <HK> byte[][] rawHashKeys(HK... hashKeys) {

        byte[][] rawHashKeys = new byte[hashKeys.length][];
        int i = 0;
        for (HK hashKey : hashKeys) {
            rawHashKeys[i++] = rawHashKey(hashKey);
        }
        return rawHashKeys;
    }

    @SuppressWarnings("unchecked")
    protected <HV> byte[] rawHashValue(HV value) {

        if (hashValueSerializer() == null && value instanceof byte[]) {
            return (byte[]) value;
        }
        return hashValueSerializer().serialize(value);
    }

    protected <T> List<T> deserializeValues(List<byte[]> rawValues) {
        if (valueSerializer() == null) {
            return (List<T>) rawValues;
        }
        return SerializationUtils.deserializeValues(rawValues, List.class, valueSerializer());
    }

    @SuppressWarnings("unchecked")
    protected  <HK> HK deserializeHashKey(byte[] value) {
        if (hashKeySerializer() == null) {
            return (HK) value;
        }
        return (HK) hashKeySerializer().deserialize(value);
    }
    @SuppressWarnings("unchecked")
    <HV> HV deserializeHashValue(byte[] value) {
        if (hashValueSerializer() == null) {
            return (HV) value;
        }
        return (HV) hashValueSerializer().deserialize(value);
    }
}
