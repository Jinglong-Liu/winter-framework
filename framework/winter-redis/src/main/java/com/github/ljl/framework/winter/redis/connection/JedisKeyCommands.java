package com.github.ljl.framework.winter.redis.connection;

import com.github.ljl.framework.winter.redis.core.ValueEncoding;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-16 13:12
 **/

class JedisKeyCommands implements RedisKeyCommands {

    private final JedisConnection connection;

    JedisKeyCommands(JedisConnection connection) {
        this.connection = connection;
    }

    @Override
    public Boolean copy(byte[] sourceKey, byte[] targetKey, boolean replace) {
        return connection.invoke().just(jedis -> jedis.copy(sourceKey, targetKey, replace));
    }

    @Override
    public Long exists(byte[]... keys) {
        return connection.invoke().just(jedis -> jedis.exists(keys));
    }

    @Override
    public Long del(byte[]... keys) {
        return connection.invoke().just(jedis -> jedis.del(keys));
    }

    @Override
    public Long unlink(byte[]... keys) {
        return connection.invoke().just(jedis -> jedis.unlink(keys));
    }

    @Override
    public Long touch(byte[]... keys) {
        return connection.invoke().just(jedis -> jedis.touch(keys));
    }

    @Override
    public Set<byte[]> keys(byte[] pattern) {
        return connection.invoke().just(jedis -> jedis.keys(pattern));
    }

    @Override
    public byte[] randomKey() {
        return connection.invoke().just(jedis -> jedis.randomBinaryKey());
    }

    @Override
    public void rename(byte[] oldKey, byte[] newKey) {
        connection.invoke().just(jedis -> jedis.rename(oldKey, newKey));
    }

    @Override
    public Boolean renameNX(byte[] oldKey, byte[] newKey) {
        return connection.invoke()
                .from(jedis -> jedis.renamenx(oldKey, newKey))
                .get(Converters.longToBoolean());
    }

    @Override
    public Boolean expire(byte[] key, long seconds) {
        return connection.invoke()
                .from(jedis -> jedis.expire(key, seconds))
                .get(Converters.longToBoolean());
    }

    @Override
    public Boolean pExpire(byte[] key, long millis) {
        return connection.invoke()
                .from(jedis -> jedis.pexpire(key, millis))
                .get(Converters.longToBoolean());
    }

    @Override
    public Boolean expireAt(byte[] key, long unixTime) {
        return connection.invoke()
                .from(jedis -> jedis.expireAt(key, unixTime))
                .get(Converters.longToBoolean());
    }

    /**
     * 设置过期时间
     * @param key
     * @param unixTimeInMillis
     * @return
     */
    @Override
    public Boolean pExpireAt(byte[] key, long unixTimeInMillis) {
        return connection.invoke()
                .from(jedis -> jedis.pexpireAt(key, unixTimeInMillis))
                .get(Converters.longToBoolean());
    }

    /**
     * 移除key的过期时间
     * @param key
     * @return
     */
    @Override
    public Boolean persist(byte[] key) {
        return connection.invoke()
                .from(jedis -> jedis.persist(key))
                .get(Converters.longToBoolean());
    }

    @Override
    public Boolean move(byte[] key, int dbIndex) {
        return connection.invoke()
                .from(jedis -> jedis.move(key, dbIndex))
                .get(Converters.longToBoolean());
    }

    @Override
    public Long ttl(byte[] key) {
        return connection.invoke().just(jedis -> jedis.ttl(key));
    }

    @Override
    public Long ttl(byte[] key, TimeUnit timeUnit) {
        return connection.invoke()
                .from(jedis -> jedis.ttl(key))
                .get(Converters.secondsToTimeUnit(timeUnit));
    }

    @Override
    public Long pTtl(byte[] key) {
        return connection.invoke().just(jedis -> jedis.pttl(key));
    }

    @Override
    public Long pTtl(byte[] key, TimeUnit timeUnit) {
        return connection.invoke()
                .from(jedis -> jedis.pttl(key))
                .get(Converters.secondsToTimeUnit(timeUnit));
    }

    /**
     * 返回value的序列化表示
     * @param key
     * @return
     */
    @Override
    public byte[] dump(byte[] key) {
        return connection.invoke().just(jedis -> jedis.dump(key));
    }

    /**
     * not support this version
     * @param key
     * @param ttlInMillis
     * @param serializedValue
     * @param replace
     */
    @Override
    public void restore(byte[] key, long ttlInMillis, byte[] serializedValue, boolean replace) {
        throw new RuntimeException("method not support: restore(byte[] key, long ttlInMillis, byte[] serializedValue, boolean replace");
    }

    @Override
    public Duration idletime(byte[] key) {
        return connection.invoke()
                .from(jedis -> jedis.objectIdletime(key))
                .get(Converters.secondsToDuration());
    }

    @Override
    public Long refcount(byte[] key) {
        return connection.invoke().just(jedis -> jedis.objectRefcount(key));
    }

    @Override
    public DataType type(byte[] key) {
        return connection.invoke()
                .from(jedis -> jedis.type(key))
                .get(Converters.stringToDataType());
    }

    @Override
    public ValueEncoding encodingOf(byte[] key) {
        return connection.invoke()
                .from(jedis -> jedis.objectEncoding(key))
                .get(Converters::toEncoding);
    }
}
