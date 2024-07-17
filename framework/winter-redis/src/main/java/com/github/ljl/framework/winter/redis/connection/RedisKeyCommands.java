package com.github.ljl.framework.winter.redis.connection;

//import org.springframework.data.redis.connection.DataType;
//import org.springframework.data.redis.connection.SortParameters;
//import org.springframework.data.redis.connection.ValueEncoding;
//import org.springframework.data.redis.core.Cursor;
//import org.springframework.data.redis.core.KeyScanOptions;
//import org.springframework.data.redis.core.ScanOptions;
//import com.github.ljl.framework.winter.redis.core.ValueEncoding;

import com.github.ljl.framework.winter.redis.core.ValueEncoding;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 19:18
 **/

public interface RedisKeyCommands {
    Boolean copy(byte[] sourceKey, byte[] targetKey, boolean replace);

    default Boolean exists(byte[] key) {
        Long count = exists(new byte[][] { key });
        return count != null ? count > 0 : null;
    }

    Long exists(byte[]... keys);

    Long del(byte[]... keys);

    Long unlink(byte[]... keys);

    Long touch(byte[]... keys);

    Set<byte[]> keys(byte[] pattern);

    byte[] randomKey();

    void rename(byte[] oldKey, byte[] newKey);

    Boolean renameNX(byte[] oldKey, byte[] newKey);

    Boolean expire(byte[] key, long seconds);

    Boolean pExpire(byte[] key, long millis);

    Boolean expireAt(byte[] key, long unixTime);

    Boolean pExpireAt(byte[] key, long unixTimeInMillis);

    Boolean persist(byte[] key);

    Boolean move(byte[] key, int dbIndex);

    Long ttl(byte[] key);

    Long ttl(byte[] key, TimeUnit timeUnit);

    Long pTtl(byte[] key);

    Long pTtl(byte[] key, TimeUnit timeUnit);

    byte[] dump(byte[] key);

    default void restore(byte[] key, long ttlInMillis, byte[] serializedValue) {
        restore(key, ttlInMillis, serializedValue, false);
    }

    void restore(byte[] key, long ttlInMillis, byte[] serializedValue, boolean replace);

    Duration idletime(byte[] key);

    Long refcount(byte[] key);

    DataType type(byte[] key);

//    default Cursor<byte[]> scan(KeyScanOptions options) {
//        return scan((ScanOptions) options);
//    }
//
//    Cursor<byte[]> scan(ScanOptions options);

//    List<byte[]> sort(byte[] key, SortParameters params);
//
//    Long sort(byte[] key, SortParameters params, byte[] storeKey);

    ValueEncoding encodingOf(byte[] key);



}
