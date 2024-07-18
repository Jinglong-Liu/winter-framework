package com.github.ljl.framework.winter.redis.connection;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-17 09:10
 **/

public interface HashOperations<H, HK, HV> {

    Long delete(H key, Object... hashKeys);

    Boolean hasKey(H key, Object hashKey);

    HV get(H key, Object hashKey);

    List<HV> multiGet(H key, Collection<HK> hashKeys);

    Long increment(H key, HK hashKey, long delta);

    Double increment(H key, HK hashKey, double delta);

    HK randomKey(H key);

    Map.Entry<HK, HV> randomEntry(H key);

    List<HK> randomKeys(H key, long count);

    Map<HK, HV> randomEntries(H key, long count);

    Set<HK> keys(H key);

    Long lengthOfValue(H key, HK hashKey);

    Long size(H key);

    void putAll(H key, Map<? extends HK, ? extends HV> m);

    void put(H key, HK hashKey, HV value);

    Boolean putIfAbsent(H key, HK hashKey, HV value);

    List<HV> values(H key);

    Map<HK, HV> entries(H key);

//    Cursor<Map.Entry<HK, HV>> scan(H key, ScanOptions options);

    RedisOperations<H, ?> getOperations();
}
