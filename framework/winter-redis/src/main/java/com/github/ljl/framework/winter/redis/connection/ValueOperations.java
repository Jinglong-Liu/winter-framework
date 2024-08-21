package com.github.ljl.framework.winter.redis.connection;

import com.github.ljl.framework.winter.redis.utils.TimeoutUtils;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface ValueOperations<K, V> extends RedisOperations {

    void set(K key, V value);

    void set(K key, V value, long timeout, TimeUnit unit);

    default void set(K key, V value, Duration timeout) {

        if (TimeoutUtils.hasMillis(timeout)) {
            set(key, value, timeout.toMillis(), TimeUnit.MILLISECONDS);
        } else {
            set(key, value, timeout.getSeconds(), TimeUnit.SECONDS);
        }
    }

    Boolean setIfAbsent(K key, V value);

    Boolean setIfAbsent(K key, V value, long timeout, TimeUnit unit);

    default Boolean setIfAbsent(K key, V value, Duration timeout) {

        if (TimeoutUtils.hasMillis(timeout)) {
            return setIfAbsent(key, value, timeout.toMillis(), TimeUnit.MILLISECONDS);
        }

        return setIfAbsent(key, value, timeout.getSeconds(), TimeUnit.SECONDS);
    }

    Boolean setIfPresent(K key, V value);

    Boolean setIfPresent(K key, V value, long timeout, TimeUnit unit);

    default Boolean setIfPresent(K key, V value, Duration timeout) {

        if (TimeoutUtils.hasMillis(timeout)) {
            return setIfPresent(key, value, timeout.toMillis(), TimeUnit.MILLISECONDS);
        }

        return setIfPresent(key, value, timeout.getSeconds(), TimeUnit.SECONDS);
    }

    V get(Object key);

    V getAndDelete(K key);

    V getAndExpire(K key, long timeout, TimeUnit unit);

    V getAndExpire(K key, Duration timeout);

    V getAndPersist(K key);

    V getAndSet(K key, V value);


    void multiSet(Map<? extends K, ? extends V> map);

    Boolean multiSetIfAbsent(Map<? extends K, ? extends V> map);

    List<V> multiGet(Collection<K> keys);

    Long increment(K key);

    Long increment(K key, long delta);

    Double increment(K key, double delta);

    Long decrement(K key);

    Long decrement(K key, long delta);

    Integer append(K key, String value);

    String get(K key, long start, long end);

    void set(K key, V value, long offset);

    Long size(K key);

    Boolean setBit(K key, long offset, boolean value);


    Boolean getBit(K key, long offset);

    List<Long> bitField(K key, BitFieldSubCommands subCommands);

    RedisOperations<K, V> getOperations();
}
