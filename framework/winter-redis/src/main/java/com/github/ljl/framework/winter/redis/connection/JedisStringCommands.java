package com.github.ljl.framework.winter.redis.connection;

import com.github.ljl.framework.winter.redis.type.Expiration;

import redis.clients.jedis.params.GetExParams;
import redis.clients.jedis.params.SetParams;

import java.util.List;
import java.util.Map;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 20:46
 **/

class JedisStringCommands<K, V> implements RedisStringCommands<K, V> {

    private final JedisConnection connection;

    public JedisStringCommands(JedisConnection connection) {
        this.connection = connection;
    }

    @Override
    public byte[] get(byte[] key) {
        return connection.invoke().just(jedis -> jedis.get(key));
    }

    @Override
    public byte[] getDel(byte[] key) {
        return connection.invoke().just(jedis -> jedis.getDel(key));
    }

    @Override
    public byte[] getEx(byte[] key, Expiration expiration) {
        GetExParams getExParams = Converters.toGetExParams(expiration);
        return connection.invoke().just(jedis -> jedis.getEx(key, getExParams));
    }

    @Override
    public byte[] getSet(byte[] key, byte[] value) {
        return connection.invoke().just(jedis -> jedis.getSet(key, value));
    }

    @Override
    public List<byte[]> mGet(byte[]... keys) {
        return connection.invoke().just(jedis -> jedis.mget(keys));
    }

    @Override
    public Boolean set(byte[] key, byte[] value) {
        return connection.invoke()
                .from(jedis -> jedis.set(key, value))
                .get(Converters.stringToBooleanConverter());
    }

    @Override
    public Boolean set(byte[] key, byte[] value, Expiration expiration, SetOption option) {
        SetParams params = Converters.toSetCommandExPxArgument(expiration,
                Converters.toSetCommandNxXxArgument(option));

        return connection.invoke()
                .from(jedis -> jedis.set(key, value, params))
                .getOrElse(Converters.stringToBooleanConverter(), () -> false);
    }

    @Override
    public Boolean setNX(byte[] key, byte[] value) {
        return connection.invoke()
                .from(jedis -> jedis.setnx(key, value))
                .get(Converters.longToBoolean());
    }

    @Override
    public Boolean setEx(byte[] key, long seconds, byte[] value) {
        return connection.invoke()
                .from(jedis -> jedis.setex(key, seconds, value))
                .get(Converters.stringToBooleanConverter());
    }

    @Override
    public Boolean pSetEx(byte[] key, long milliseconds, byte[] value) {
        return connection.invoke()
                .from(jedis -> jedis.psetex(key, milliseconds, value))
                .get(Converters.stringToBooleanConverter());
    }

    @Override
    public Boolean mSet(Map<byte[], byte[]> tuple) {
        return connection.invoke()
                .from(jedis -> jedis.mset(Converters.toByteArrays(tuple)))
                .get(Converters.stringToBooleanConverter());
    }

    @Override
    public Boolean mSetNX(Map<byte[], byte[]> tuple) {
        return connection.invoke()
                .from(jedis -> jedis.msetnx(Converters.toByteArrays(tuple)))
                .get(Converters.longToBoolean());
    }

    @Override
    public Long incr(byte[] key) {
        return connection.invoke().just(jedis -> jedis.incr(key));
    }

    @Override
    public Long incrBy(byte[] key, long value) {
        return connection.invoke().just(jedis -> jedis.incrBy(key, value));
    }

    @Override
    public Double incrBy(byte[] key, double value) {
        return connection.invoke().just(jedis -> jedis.incrByFloat(key, value));
    }

    @Override
    public Long decr(byte[] key) {
        return connection.invoke().just(jedis -> jedis.decr(key));
    }

    @Override
    public Long decrBy(byte[] key, long value) {
        return connection.invoke().just(jedis -> jedis.decrBy(key, value));
    }

    @Override
    public Long append(byte[] key, byte[] value) {
        return connection.invoke().just(jedis -> jedis.append(key, value));
    }

    @Override
    public byte[] getRange(byte[] key, long start, long end) {
        return connection.invoke().just(jedis -> jedis.getrange(key, start, end));
    }

    @Override
    public void setRange(byte[] key, byte[] value, long offset) {
        connection.invoke().just(jedis -> jedis.setrange(key, offset, value));
    }

    @Override
    public Boolean getBit(byte[] key, long offset) {
        return connection.invoke().just(jedis -> jedis.getbit(key, offset));
    }

    @Override
    public Boolean setBit(byte[] key, long offset, boolean value) {
        return connection.invoke().just(jedis -> jedis.setbit(key, offset, value));
    }

    @Override
    public Long bitCount(byte[] key) {
        return connection.invoke().just(jedis -> jedis.bitcount(key));
    }

    @Override
    public Long bitCount(byte[] key, long start, long end) {
        return connection.invoke().just(jedis -> jedis.bitcount(key, start, end));
    }

//    @Override
//    public List<Long> bitField(byte[] key, BitFieldSubCommands subCommands) {
//        return null;
//    }

    @Override
    public Long bitOp(BitOperation op, byte[] destination, byte[]... keys) {
        return connection.invoke().just(jedis -> jedis.bitop(Converters.toBitOp(op), destination, keys));
    }

//    @Override
//    public Long bitPos(byte[] key, boolean bit, Range<Long> range) {
//        return null;
//    }

    @Override
    public Long strLen(byte[] key) {
        return connection.invoke().just(jedis -> jedis.strlen(key));
    }
}

enum SetOption {

    /**
     * Do not set any additional command argument.
     */
    UPSERT,

    /**
     * {@code NX}
     */
    SET_IF_ABSENT,

    /**
     * {@code XX}
     */
    SET_IF_PRESENT;

    /**
     * Do not set any additional command argument.
     */
    public static SetOption upsert() {
        return UPSERT;
    }

    /**
     * {@code XX}
     */
    public static SetOption ifPresent() {
        return SET_IF_PRESENT;
    }

    /**
     * {@code NX}
     */
    public static SetOption ifAbsent() {
        return SET_IF_ABSENT;
    }
}
