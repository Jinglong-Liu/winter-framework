package com.github.ljl.framework.winter.redis.connection;

import com.github.ljl.framework.winter.redis.template.RedisTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-17 09:13
 **/

public class StandardHashOperations <K, HK, HV> extends AbstractOperations<K, Object> implements HashOperations<K, HK, HV> {

    public <V> StandardHashOperations(RedisTemplate<K, Object> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    public Boolean hasKey(K key, Object hashKey) {
        byte[] rawKey = rawKey(key);
        byte[] rawHashKeys = rawHashKey(hashKey);
        return execute(connection -> connection.hExists(rawKey, rawHashKeys));
    }

    @Override
    public HV get(K key, Object hashKey) {
        byte[] rawKey = rawKey(key);
        byte[] rawHashKey = rawHashKey(hashKey);
        byte[] rawValue = execute(connection -> connection.hGet(rawKey, rawHashKey));
        return deserializeValue(rawValue);
    }

    @Override
    public List<HV> multiGet(K key, Collection<HK> hashKeys) {
        byte[] rawKey = rawKey(key);
        byte[][] fields = hashKeys.stream()
                .map(this::rawHashKey)
                .collect(Collectors.toList())
                .toArray(new byte[0][0]);
        // List<byte[]> -> List<HV>
        List<byte[]> result = execute(connection -> connection.hMGet(rawKey, fields));
        return deserializeValues(result);
    }

    @Override
    public Long increment(K key, HK hashKey, long delta) {
        byte[] rawKey = rawKey(key);
        byte[] rawHashKey = rawHashKey(hashKey);
        return execute(connection -> connection.hIncrBy(rawKey, rawHashKey, delta));
    }

    @Override
    public Double increment(K key, HK hashKey, double delta) {
        byte[] rawKey = rawKey(key);
        byte[] rawHashKey = rawHashKey(hashKey);
        return execute(connection -> connection.hIncrBy(rawKey, rawHashKey, delta));
    }

    @Override
    public HK randomKey(K key) {
        byte[] rawKey = rawKey(key);
        byte[] result = execute(connection -> connection.hRandField(rawKey));
        return deserializeValue(result);
    }

    @Override
    public Map.Entry<HK, HV> randomEntry(K key) {
        byte[] rawKey = rawKey(key);
        Map.Entry<byte[], byte[]> rawEntry = execute(connection -> connection.hRandFieldWithValues(rawKey));
        return rawEntry == null ? null
                : Converters.entryOf(deserializeHashKey(rawEntry.getKey()), deserializeHashValue(rawEntry.getValue()));
    }

    @Override
    public List<HK> randomKeys(K key, long count) {
        byte[] rawKey = rawKey(key);
        List<byte[]> rawValues = execute(connection -> connection.hRandField(rawKey, count));
        return deserializeValues(rawValues);
    }

    @Override
    public Map<HK, HV> randomEntries(K key, long count) {
        byte[] rawKey = rawKey(key);

        List<Map.Entry<byte[], byte[]>> rawEntries =
                execute(connection -> connection.hRandFieldWithValues(rawKey, count));

        // List<Map.Entry> -> Map<HK, HV>
        return  rawEntries.stream().collect(Collectors.toMap(
                entry -> deserializeHashKey(entry.getKey()),
                entry -> deserializeHashValue(entry.getValue()),
                (oldValue, newValue) -> newValue,
                LinkedHashMap::new
        ));
    }

    @Override
    public Set<HK> keys(K key) {
        byte[] rawKey = rawKey(key);
        Set<byte[]> rawSet = execute(connection -> connection.hKeys(rawKey));

        Set<HK> result = new HashSet<>();
        rawSet.forEach(hKey -> result.add(deserializeHashKey(hKey)));
        return result;
//        return rawSet.stream()
//                .map(hKey -> (HK) deserializeHashKey(hKey))
//                .collect(Collectors.toSet());
    }

    /**
     * @param key: key
     * @param hashKey: field
     * @return value.length()
     */
    @Override
    public Long lengthOfValue(K key, HK hashKey) {
        byte[] rawKey = rawKey(key);
        byte[] rawHashKey = rawHashKey(hashKey);
        return execute(connection -> connection.hStrLen(rawKey, rawHashKey));
    }

    /**
     * hLen -> size
     * @param key
     * @return size of field (of this key)
     */
    @Override
    public Long size(K key) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> connection.hLen(rawKey));
    }

    @Override
    public void putAll(K key, Map<? extends HK, ? extends HV> m) {
        byte[] rawKey = rawKey(key);
        Map<byte[], byte[]> hashes = m.entrySet().stream().collect(Collectors.toMap(
                entry -> rawHashKey(entry.getKey()),
                entry -> rawHashValue(entry.getValue()),
                (oldValue, newValue) -> newValue,
                LinkedHashMap::new
        ));
        // void hMSet(byte[] key, Map<byte[], byte[]> hashes);
        execute(connection -> {
            connection.hMSet(rawKey, hashes);
            return null;
        });
    }

    @Override
    public void put(K key, HK hashKey, HV value) {
        byte[] rawKey = rawKey(key);
        byte[] rawHashKey = rawHashKey(hashKey);
        byte[] rawValue = rawHashValue(value);
        execute(connection -> {
            connection.hSet(rawKey, rawHashKey, rawValue);
            return null;
        });
    }

    @Override
    public Boolean putIfAbsent(K key, HK hashKey, HV value) {
        byte[] rawKey = rawKey(key);
        byte[] rawHashKey = rawHashKey(hashKey);
        byte[] rawValue = rawHashValue(value);
        return execute(connection -> connection.hSetNX(rawKey, rawHashKey, rawValue));
    }

    @Override
    public List<HV> values(K key) {
        byte[] rawKey = rawKey(key);
        List<byte[]> rawValues =  execute(connection -> connection.hVals(rawKey));
        return deserializeValues(rawValues);
    }

    @Override
    public Long delete(K key, Object... hashKeys) {
        byte[] rawKey = rawKey(key);
        byte[][] rawHashKeys = rawHashKeys(hashKeys);
        return execute(connection -> connection.hDel(rawKey, rawHashKeys));
    }

    @Override
    public Map<HK, HV> entries(K key) {
        byte[] rawKey = rawKey(key);
        Map<byte[], byte[]> rawMap = execute(connection -> connection.hGetAll(rawKey));
        return rawMap.entrySet().stream().collect(Collectors.toMap(
                entry -> deserializeHashKey(entry.getKey()),
                entry -> deserializeHashValue(entry.getValue()),
                (oldValue, newValue) -> newValue,
                LinkedHashMap::new
        ));
    }

    @Override
    public RedisOperations<K, ?> getOperations() {
        return template;
    }
}
