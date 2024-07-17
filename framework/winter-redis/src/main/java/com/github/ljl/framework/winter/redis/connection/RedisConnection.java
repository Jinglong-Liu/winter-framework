package com.github.ljl.framework.winter.redis.connection;

import com.github.ljl.framework.winter.redis.utils.TimeoutUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 18:44
 **/

public interface RedisConnection<K, V> extends RedisCommands {

    // <T> T execute(RedisCallback<T> action);
//
//    Boolean copy(K sourceKey, K targetKey, boolean replace);
//
//    Boolean hasKey(K key);
//
//    Long countExistingKeys(Collection<K> keys);
//
//    Boolean delete(K key);
//
//    Long delete(Collection<K> keys);
//
//    Boolean unlink(K key);
//
//    Long unlink(Collection<K> keys);
//
//    DataType type(K key);
//
//    Set<K> keys(K pattern);
//
//    K randomKey();
//
//    void rename(K oldKey, K newKey);
//
//    Boolean renameIfAbsent(K oldKey, K newKey);
//
//    Boolean expire(K key, long timeout, TimeUnit unit);
//
//    default Boolean expire(K key, Duration timeout) {
//
//        assert timeout != null;
//
//        return TimeoutUtils.hasMillis(timeout) ? expire(key, timeout.toMillis(), TimeUnit.MILLISECONDS)
//                : expire(key, timeout.getSeconds(), TimeUnit.SECONDS);
//    }
//
//    Boolean expireAt(K key, Date date);
//
//    default Boolean expireAt(K key, Instant expireAt) {
//
//        assert expireAt != null;
//
//        return expireAt(key, Date.from(expireAt));
//    }
//
//    Boolean persist(K key);
//
//    Boolean move(K key, int dbIndex);
//
//    byte[] dump(K key);
//
//    default void restore(K key, byte[] value, long timeToLive, TimeUnit unit) {
//        restore(key, value, timeToLive, unit, false);
//    }
//
//    void restore(K key, byte[] value, long timeToLive, TimeUnit unit, boolean replace);
//
//    Long getExpire(K key);
//
//    Long getExpire(K key, TimeUnit timeUnit);
}
