package com.github.ljl.framework.winter.redis.connection;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-17 09:15
 **/

interface RedisHashCommands {

    Boolean hSet(byte[] key, byte[] field, byte[] value);

    Boolean hSetNX(byte[] key, byte[] field, byte[] value);

    byte[] hGet(byte[] key, byte[] field);

    List<byte[]> hMGet(byte[] key, byte[]... fields);

    void hMSet(byte[] key, Map<byte[], byte[]> hashes);

    Long hIncrBy(byte[] key, byte[] field, long delta);

    Double hIncrBy(byte[] key, byte[] field, double delta);

    Boolean hExists(byte[] key, byte[] field);

    Long hDel(byte[] key, byte[]... fields);

    Long hLen(byte[] key);

    Set<byte[]> hKeys(byte[] key);

    List<byte[]> hVals(byte[] key);

    Map<byte[], byte[]> hGetAll(byte[] key);

    byte[] hRandField(byte[] key);

    Map.Entry<byte[], byte[]> hRandFieldWithValues(byte[] key);

    List<byte[]> hRandField(byte[] key, long count);

    List<Map.Entry<byte[], byte[]>> hRandFieldWithValues(byte[] key, long count);

    // Cursor<Map.Entry<byte[], byte[]>> hScan(byte[] key, ScanOptions options);

    Long hStrLen(byte[] key, byte[] field);
}
