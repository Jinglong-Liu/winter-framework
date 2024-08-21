package com.github.ljl.framework.winter.redis.connection;

public interface RedisConnectionPool {
    RedisConnection getConnection() throws InterruptedException;

    void returnConnection(RedisConnection connection);

    void close();
}
