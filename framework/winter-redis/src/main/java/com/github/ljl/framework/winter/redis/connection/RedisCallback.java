package com.github.ljl.framework.winter.redis.connection;

public interface RedisCallback<T> {
    T doInRedis(RedisConnection connection);
}
