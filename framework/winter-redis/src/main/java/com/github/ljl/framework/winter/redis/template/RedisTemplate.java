package com.github.ljl.framework.winter.redis.template;

import com.github.ljl.framework.winter.redis.connection.*;
import com.github.ljl.framework.winter.redis.serializer.RedisSerializer;
import com.github.ljl.framework.winter.redis.serializer.StringRedisSerializer;
import lombok.Getter;
import redis.clients.jedis.JedisClientConfig;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 18:20
 **/

public class RedisTemplate<K, V> implements RedisOperations {

    @Resource
    private JedisClientConfig jedisClientConfig;

    @Resource
    private RedisConnectionPool connectionPool;

    @Getter
    private Class<K> keyType = null;

    @Getter
    private Class<V> valueType = null;

    //private final ConcurrentHashMap<Class<?>, StandardValueOperation<K, V>> valueOpsCache = new ConcurrentHashMap<>();

    //private final Map<String, StandardValueOperation<K, V>> valueOpsCache = new HashMap<>();

    private static final Map<String, StandardValueOperation> valueOpsCache = new ConcurrentHashMap<>();

    public RedisTemplate(Class<K> keyType, Class<V> valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    // 真正执行execute
    // 连接全权由连接池管理

    public <T> T execute(RedisCallback<T> action) {
        RedisConnection connection = null;
        try {
            connection = connectionPool.getConnection();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        T result = action.doInRedis(connection);
        connectionPool.returnConnection(connection);
        return result;
    }

    public RedisSerializer getKeySerializer() {
        return StringRedisSerializer.get();
    }

    public StandardValueOperation<K, V> opsForValues() {
        // return new StandardValueOperation<K, V>(this, keyType, valueType);
        // Generate a key for the cache map based on the types
        // 怎么感觉会更慢。。。
        // Class<?> cacheKey = new CacheKey<>(keyType, valueType).getClass();
        String key = "valueOps" + keyType.getName() + valueType.getName();
        return valueOpsCache.computeIfAbsent(key, k -> new StandardValueOperation<>(this, keyType, valueType));
    }

    static class CacheKey<K, V> {
        private final Class<K> keyType;
        private final Class<V> valueType;

        CacheKey(Class<K> keyType, Class<V> valueType) {
            this.keyType = keyType;
            this.valueType = valueType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey<?, ?> cacheKey = (CacheKey<?, ?>) o;
            return keyType.equals(cacheKey.keyType) && valueType.equals(cacheKey.valueType);
        }

        @Override
        public int hashCode() {
            int result = keyType.hashCode();
            result = 31 * result + valueType.hashCode();
            return result;
        }
    }
}
