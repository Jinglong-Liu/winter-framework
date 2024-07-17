package com.github.ljl.framework.winter.redis.connection;

import com.github.ljl.framework.winter.redis.core.ValueEncoding;
import com.github.ljl.framework.winter.redis.type.Expiration;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-16 14:30
 **/

abstract class AbstractRedisConnectionDelegate implements RedisCommands {

    public abstract RedisKeyCommands keyCommands();

    public abstract RedisStringCommands stringCommands();

    @Override
    public Boolean copy(byte[] sourceKey, byte[] targetKey, boolean replace) {
        return keyCommands().copy(sourceKey, targetKey, replace);
    }

    @Override
    public Long exists(byte[]... keys) {
        return keyCommands().exists(keys);
    }

    @Override
    public Long del(byte[]... keys) {
        return keyCommands().del(keys);
    }

    @Override
    public Long unlink(byte[]... keys) {
        return keyCommands().unlink(keys);
    }

    @Override
    public Long touch(byte[]... keys) {
        return keyCommands().touch(keys);
    }

    @Override
    public Set<byte[]> keys(byte[] pattern) {
        return keyCommands().keys(pattern);
    }

    @Override
    public byte[] randomKey() {
        return keyCommands().randomKey();
    }

    @Override
    public void rename(byte[] oldKey, byte[] newKey) {
        keyCommands().rename(oldKey, newKey);
    }

    @Override
    public Boolean renameNX(byte[] oldKey, byte[] newKey) {
        return keyCommands().renameNX(oldKey, newKey);
    }

    @Override
    public Boolean expire(byte[] key, long seconds) {
        return keyCommands().expire(key, seconds);
    }

    @Override
    public Boolean pExpire(byte[] key, long millis) {
        return keyCommands().pExpire(key, millis);
    }

    @Override
    public Boolean expireAt(byte[] key, long unixTime) {
        return keyCommands().expireAt(key, unixTime);
    }

    @Override
    public Boolean pExpireAt(byte[] key, long unixTimeInMillis) {
        return keyCommands().pExpireAt(key, unixTimeInMillis);
    }

    @Override
    public Boolean persist(byte[] key) {
        return keyCommands().persist(key);
    }

    @Override
    public Boolean move(byte[] key, int dbIndex) {
        return keyCommands().move(key, dbIndex);
    }

    @Override
    public Long ttl(byte[] key) {
        return keyCommands().ttl(key);
    }

    @Override
    public Long ttl(byte[] key, TimeUnit timeUnit) {
        return keyCommands().ttl(key, timeUnit);
    }

    @Override
    public Long pTtl(byte[] key) {
        return keyCommands().pTtl(key);
    }

    @Override
    public Long pTtl(byte[] key, TimeUnit timeUnit) {
        return keyCommands().pTtl(key, timeUnit);
    }

    @Override
    public byte[] dump(byte[] key) {
        return keyCommands().dump(key);
    }

    @Override
    public void restore(byte[] key, long ttlInMillis, byte[] serializedValue, boolean replace) {
        keyCommands().restore(key, ttlInMillis, serializedValue, replace);
    }

    @Override
    public Duration idletime(byte[] key) {
        return keyCommands().idletime(key);
    }

    @Override
    public Long refcount(byte[] key) {
        return keyCommands().refcount(key);
    }

    @Override
    public DataType type(byte[] key) {
        return keyCommands().type(key);
    }

    @Override
    public ValueEncoding encodingOf(byte[] key) {
        return keyCommands().encodingOf(key);
    }

    @Override
    public byte[] get(byte[] key) {
        return stringCommands().get(key);
    }

    @Override
    public byte[] getDel(byte[] key) {
        return stringCommands().getDel(key);
    }

    @Override
    public byte[] getEx(byte[] key, Expiration expiration) {
        return stringCommands().getEx(key, expiration);
    }

    @Override
    public byte[] getSet(byte[] key, byte[] value) {
        return stringCommands().getSet(key, value);
    }

    @Override
    public List<byte[]> mGet(byte[]... keys) {
        return stringCommands().mGet(keys);
    }

    @Override
    public Boolean set(byte[] key, byte[] value) {
        return stringCommands().set(key, value);
    }

    @Override
    public Boolean set(byte[] key, byte[] value, Expiration expiration, SetOption option) {
        return stringCommands().set(key, value, expiration, option);
    }

    @Override
    public Boolean setNX(byte[] key, byte[] value) {
        return stringCommands().setNX(key, value);
    }

    @Override
    public Boolean setEx(byte[] key, long seconds, byte[] value) {
        return stringCommands().setEx(key, seconds, value);
    }

    @Override
    public Boolean pSetEx(byte[] key, long milliseconds, byte[] value) {
        return stringCommands().pSetEx(key, milliseconds, value);
    }

    @Override
    public Long incr(byte[] key) {
        return stringCommands().incr(key);
    }

    @Override
    public Long incrBy(byte[] key, long value) {
        return stringCommands().incrBy(key, value);
    }

    @Override
    public Double incrBy(byte[] key, double value) {
        return stringCommands().incrBy(key,value);
    }

    @Override
    public Long decr(byte[] key) {
        return stringCommands().decr(key);
    }

    @Override
    public Long decrBy(byte[] key, long value) {
        return stringCommands().decrBy(key, value);
    }

    @Override
    public Long append(byte[] key, byte[] value) {
        return stringCommands().append(key, value);
    }

    @Override
    public byte[] getRange(byte[] key, long start, long end) {
        return stringCommands().getRange(key, start, end);
    }

    @Override
    public void setRange(byte[] key, byte[] value, long offset) {
        stringCommands().setRange(key, value, offset);
    }

    @Override
    public Boolean getBit(byte[] key, long offset) {
        return stringCommands().getBit(key, offset);
    }

    @Override
    public Boolean setBit(byte[] key, long offset, boolean value) {
        return stringCommands().setBit(key, offset, value);
    }

    @Override
    public Long bitCount(byte[] key) {
        return stringCommands().bitCount(key);
    }

    @Override
    public Long bitCount(byte[] key, long start, long end) {
        return stringCommands().bitCount(key, start, end);
    }

    @Override
    public Long bitOp(BitOperation op, byte[] destination, byte[]... keys) {
        return stringCommands().bitOp(op, destination, keys);
    }

    @Override
    public Long strLen(byte[] key) {
        return stringCommands().strLen(key);
    }

    @Override
    public Boolean mSetNX(Map tuple) {
        return stringCommands().mSetNX(tuple);
    }

    @Override
    public Boolean mSet(Map tuple) {
        return stringCommands().mSet(tuple);
    }
}
