package com.github.ljl.framework.winter.redis.connection;

import com.github.ljl.framework.winter.redis.type.Expiration;

import java.util.List;
import java.util.Map;

public interface RedisStringCommands<K, V> {

    byte[] get(byte[] key);
    byte[] getDel(byte[] key);
    byte[] getEx(byte[] key, Expiration expiration);
    byte[] getSet(byte[] key, byte[] value);
    List<byte[]> mGet(byte[]... keys);
    Boolean set(byte[] key, byte[] value);
    Boolean set(byte[] key, byte[] value, Expiration expiration, SetOption option);
    Boolean setNX(byte[] key, byte[] value);
    Boolean setEx(byte[] key, long seconds, byte[] value);
    Boolean pSetEx(byte[] key, long milliseconds, byte[] value);
    Boolean mSet(Map<byte[], byte[]> tuple);
    Boolean mSetNX(Map<byte[], byte[]> tuple);
    Long incr(byte[] key);
    Long incrBy(byte[] key, long value);
    Double incrBy(byte[] key, double value);
    Long decr(byte[] key);
    Long decrBy(byte[] key, long value);
    Long append(byte[] key, byte[] value);
    byte[] getRange(byte[] key, long start, long end);
    void setRange(byte[] key, byte[] value, long offset);
    Boolean getBit(byte[] key, long offset);
    Boolean setBit(byte[] key, long offset, boolean value);
    Long bitCount(byte[] key);
    Long bitCount(byte[] key, long start, long end);
    Long bitOp(BitOperation op, byte[] destination, byte[]... keys);
    Long strLen(byte[] key);
}
enum BitOperation {
    AND, OR, XOR, NOT;
}
