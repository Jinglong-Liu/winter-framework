package com.github.ljl.framework.winter.redis.connection;

import com.github.ljl.framework.winter.redis.template.RedisTemplate;
import com.github.ljl.framework.winter.redis.type.Expiration;
import com.github.ljl.framework.winter.redis.utils.SerializationUtils;
import com.github.ljl.framework.winter.redis.utils.TimeoutUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 20:28
 **/

public class StandardValueOperation<K, V> extends AbstractOperations<K,V> implements ValueOperations<K,V> {

    public StandardValueOperation(RedisTemplate<K, V> redisTemplate, Class<?> kType, Class<?> vType) {
        super(redisTemplate, kType, vType);
    }

    @Override
    public void set(K key, V value) {
        byte[] rawValue = rawValue(value);
        execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                connection.set(rawKey, rawValue);
                return null;
            }
        });
    }

    @Override
    public void set(K key, V value, long timeout, TimeUnit unit) {
        byte[] rawKey = rawKey(key);
        byte[] rawValue = rawValue(value);

        execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws RuntimeException {
                if (!TimeUnit.MILLISECONDS.equals(unit) || !failsafeInvokePsetEx(connection)) {
                    connection.setEx(rawKey, TimeoutUtils.toSeconds(timeout, unit), rawValue);
                }
                return null;
            }
            private boolean failsafeInvokePsetEx(RedisConnection connection) {
                boolean failed = false;
                try {
                    connection.pSetEx(rawKey, timeout, rawValue);
                } catch (UnsupportedOperationException e) {
                    // in case the connection does not support pSetEx return false to allow fallback to other operation.
                    failed = true;
                }
                return !failed;
            }
        });
    }

    @Override
    public Boolean setIfAbsent(K key, V value) {
        byte[] rawKey = rawKey(key);
        byte[] rawValue = rawValue(value);
        return execute(connection -> connection.setNX(rawKey, rawValue));
    }

    @Override
    public Boolean setIfAbsent(K key, V value, long timeout, TimeUnit unit) {
        byte[] rawKey = rawKey(key);
        byte[] rawValue = rawValue(value);

        Expiration expiration = Expiration.from(timeout, unit);
        return execute(connection -> connection.set(rawKey, rawValue, expiration, SetOption.ifAbsent()));
    }

    @Override
    public Boolean setIfPresent(K key, V value) {
        byte[] rawKey = rawKey(key);
        byte[] rawValue = rawValue(value);

        return execute(connection -> connection.set(rawKey, rawValue, Expiration.persistent(), SetOption.ifPresent()));
    }

    @Override
    public Boolean setIfPresent(K key, V value, long timeout, TimeUnit unit) {
        return null;
    }

    @Override
    public V get(Object key) {
        return execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                    return connection.get(rawKey);
                }
        });
    }

    <T> T execute(RedisCallback<T> callback) {
        return template.execute(callback);
    }

    @Override
    public V getAndDelete(K key) {
        return execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                return connection.getDel(rawKey);
            }
        });
    }

    /**
     * @param key
     * @param timeout
     * @param unit
     * @return
     * @since redis 6.2
     */
    @Override
    public V getAndExpire(K key, long timeout, TimeUnit unit) {
        return execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                return connection.getEx(rawKey, Expiration.from(timeout, unit));
            }
        });
    }

    @Override
    public V getAndExpire(K key, Duration timeout) {
        return null;
    }

    @Override
    public V getAndPersist(K key) {
        return null;
    }

    @Override
    public V getAndSet(K key, V newValue) {
        byte[] rawValue = rawValue(newValue);
        return execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                return connection.getSet(rawKey, rawValue);
            }
        });
    }

    //
    @Override
    public void multiSet(Map<? extends K, ? extends V> map) {
        Map<byte[], byte[]> tuple = map.entrySet().stream().collect(Collectors.toMap(
                entry -> rawKey(entry.getKey()),
                entry -> rawValue(entry.getValue()),
                (oldValue, newValue) -> newValue,
                LinkedHashMap::new
        ));
        execute(connection -> connection.mSet(tuple));
    }

    @Override
    public Boolean multiSetIfAbsent(Map<? extends K, ? extends V> map) {
        Map<byte[], byte[]> tuple = map.entrySet().stream().collect(Collectors.toMap(
                entry -> rawKey(entry.getKey()),
                entry -> rawValue(entry.getValue()),
                (oldValue, newValue) -> newValue,
                LinkedHashMap::new
        ));
        return execute(connection -> connection.mSetNX(tuple));
    }

    @Override
    public List<V> multiGet(Collection<K> keys) {
        byte[][] rawKeys = keys.stream().map(key -> rawKey(key)).toArray(byte[][]::new);
        List<byte[]> rawResult = execute(connection -> connection.mGet(rawKeys));
        return deserializeValues(rawResult);
    }

    @Override
    public Long increment(K key) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> connection.incr(rawKey));
    }

    @Override
    public Long increment(K key, long delta) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> connection.incrBy(rawKey, delta));
    }

    @Override
    public Double increment(K key, double delta) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> connection.incrBy(rawKey, delta));
    }

    @Override
    public Long decrement(K key) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> connection.decr(rawKey));
    }

    @Override
    public Long decrement(K key, long delta) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> connection.decrBy(rawKey, delta));
    }

    @Override
    public Integer append(K key, String value) {
        byte[] rawKey = rawKey(key);
        byte[] rawValue = rawValue(value);
        return execute(connection -> {
            Long result = connection.append(rawKey, rawValue);
            return (result != null) ? result.intValue() : null;
        });
    }

    @Override
    public String get(K key, long start, long end) {
        byte[] rawKey = rawKey(key);
        byte[] rawReturn = execute(connection -> connection.getRange(rawKey, start, end));
        return deserializeString(rawReturn);
    }

    @Override
    public void set(K key, V value, long offset) {
        byte[] rawKey = rawKey(key);
        byte[] rawValue = rawValue(value);

        execute(connection -> {
            connection.setRange(rawKey, rawValue, offset);
            return null;
        });
    }

    @Override
    public Long size(K key) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> connection.strLen(rawKey));
    }

    @Override
    public Boolean setBit(K key, long offset, boolean value) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> connection.setBit(rawKey, offset, value));
    }

    @Override
    public Boolean getBit(K key, long offset) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> connection.getBit(rawKey, offset));
    }

    @Override
    public RedisOperations<K, V> getOperations() {
        return template;
    }

    String deserializeString(byte[] value) {
        return (String) stringSerializer().deserialize(value);
    }

    List<V> deserializeValues(List<byte[]> rawValues) {
        if (valueSerializer() == null) {
            return (List<V>) rawValues;
        }
        return SerializationUtils.deserializeValues(rawValues, List.class, valueSerializer());
    }
}
