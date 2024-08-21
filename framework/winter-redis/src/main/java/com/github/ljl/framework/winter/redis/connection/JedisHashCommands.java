package com.github.ljl.framework.winter.redis.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-17 09:15
 **/

class JedisHashCommands implements RedisHashCommands {

    private final JedisConnection connection;

    JedisHashCommands(JedisConnection connection) {
        this.connection = connection;
    }

    @Override
    public Boolean hSet(byte[] key, byte[] field, byte[] value) {
        return connection.invoke()
                .from(jedis -> jedis.hset(key, field, value))
                .get(Converters.longToBoolean());
    }

    @Override
    public Boolean hSetNX(byte[] key, byte[] field, byte[] value) {
        return connection.invoke()
                .from(jedis -> jedis.hsetnx(key, field, value))
                .get(Converters.longToBoolean());
    }

    @Override
    public byte[] hGet(byte[] key, byte[] field) {
        return connection.invoke().just(jedis -> jedis.hget(key, field));
    }

    @Override
    public List<byte[]> hMGet(byte[] key, byte[]... fields) {
        return connection.invoke().just(jedis -> jedis.hmget(key, fields));
    }

    @Override
    public void hMSet(byte[] key, Map<byte[], byte[]> hashes) {
        connection.invoke().just(jedis -> jedis.hmset(key, hashes));
    }

    @Override
    public Long hIncrBy(byte[] key, byte[] field, long delta) {
        return connection.invoke().just(jedis -> jedis.hincrBy(key, field, delta));
    }

    @Override
    public Double hIncrBy(byte[] key, byte[] field, double delta) {
        return connection.invoke().just(jedis -> jedis.hincrByFloat(key, field, delta));
    }

    @Override
    public Boolean hExists(byte[] key, byte[] field) {
        return connection.invoke().just(jedis -> jedis.hexists(key, field));
    }

    @Override
    public Long hDel(byte[] key, byte[]... fields) {
        return connection.invoke().just(jedis -> jedis.hdel(key, fields));
    }

    @Override
    public Long hLen(byte[] key) {
        return connection.invoke().just(jedis -> jedis.hlen(key));
    }

    @Override
    public Set<byte[]> hKeys(byte[] key) {
        return connection.invoke().just(jedis -> jedis.hkeys(key));
    }

    @Override
    public List<byte[]> hVals(byte[] key) {
        return connection.invoke().just(jedis -> jedis.hvals(key));
    }

    @Override
    public Map<byte[], byte[]> hGetAll(byte[] key) {
        return connection.invoke().just(jedis -> jedis.hgetAll(key));
    }

    @Override
    public byte[] hRandField(byte[] key) {
        return connection.invoke().just(jedis -> jedis.hrandfield(key));
    }

    /**
     * hash中随机返回若干字段
     * @param key
     * @return
     */
    @Override
    public Map.Entry<byte[], byte[]> hRandFieldWithValues(byte[] key) {
        return connection.invoke()
                .from(jedis -> jedis.hrandfieldWithValues(key, 1L))
                .get(it -> it.isEmpty() ? null: it.entrySet().iterator().next());
    }

    @Override
    public List<byte[]> hRandField(byte[] key, long count) {
        return null;
    }

    @Override
    public List<Map.Entry<byte[], byte[]>> hRandFieldWithValues(byte[] key, long count) {
        // Map<byte[], byte[]> -> List<Map.Entry<byte[], byte[]>>
        return connection.invoke()
                .from(jedis -> jedis.hrandfieldWithValues(key, count))
                .get(map -> new ArrayList<>(map.entrySet()));
    }

    @Override
    public Long hStrLen(byte[] key, byte[] field) {
        return connection.invoke().just(jedis -> jedis.hstrlen(key, field));
    }
}
