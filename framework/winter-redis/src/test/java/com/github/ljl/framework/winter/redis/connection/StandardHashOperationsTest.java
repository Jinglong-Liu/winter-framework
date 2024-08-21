package com.github.ljl.framework.winter.redis.connection;

import com.github.ljl.framework.winter.boot.WinterApplication;
import com.github.ljl.framework.winter.context.context.ConfigurableApplicationContext;
import com.github.ljl.framework.winter.redis.template.RedisTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.ljl.framework.winter.redis.connection.TestConfig.*;

class StandardHashOperationsTest {
    private static ConfigurableApplicationContext applicationContext;

    private static RedisTemplate hashStringRedisTemplate;

    private static Jedis jedisClient;

    @BeforeAll
    public static void init() {
        applicationContext = (ConfigurableApplicationContext) WinterApplication.run(StandardValueOperationTest.class);
        //  redisTemplate = applicationContext.getBean(RedisTemplate.class);
        //  redisTemplate = applicationContext.getBean("redisTemplate");
        //  objectRedisTemplate = applicationContext.getBean("hashObjectRedisTemplate", RedisTemplate.class);
        //  mapRedisTemplate = applicationContext.getBean("hashMapRedisTemplate", RedisTemplate.class);
        hashStringRedisTemplate = applicationContext.getBean("hashStringRedisTemplate", RedisTemplate.class);

        jedisClient = new Jedis("localhost",6379);
        assert jedisClient.ping().equals("PONG");
    }

    @AfterAll
    public static void destroy() {
        applicationContext.close();
    }

    @Test
    void testHasKey() {
        StandardHashOperations hashOperations = hashStringRedisTemplate.opsForHash();
        String key = "hash-test-has-key";
        String invalidKey = "hash-test-has-key-invalid";
        String hashKey1 = "hash-hasKey-key1";
        String hashKey2 = "hash-hasKey-key2";
        String hashKey3 = "hash-hasKey-key3";
        String value1 = "hash-hasKey-value1";
        String value2 = "hash-hasKey-value2";
        jedisClient.del(new String[]{key, invalidKey});
        Boolean hasKey1 = hashOperations.hasKey(key, hashKey1);
        Assertions.assertFalse(hasKey1);
        jedisClient.hset(key, hashKey1, value1);
        jedisClient.hset(key, hashKey2, value2);
        Assertions.assertTrue(hashOperations.hasKey(key, hashKey1));
        Assertions.assertTrue(hashOperations.hasKey(key, hashKey2));
        Assertions.assertFalse(hashOperations.hasKey(key, hashKey3));
        Assertions.assertFalse(hashOperations.hasKey(invalidKey, hashKey1));

        jedisClient.del(new String[]{key, invalidKey});
    }

    @Test
    void testGet() {
        StandardHashOperations hashOperations = hashStringRedisTemplate.opsForHash();
        String key = "hash-test-get";
        String field = "hash-test-get-field";
        String field1 = "Hash-test-get-field";
        String value = "hash-test-get-value";
        jedisClient.del(key);
        Assertions.assertNull(hashOperations.get(key, field));
        jedisClient.hset(key, field, value);
        Assertions.assertEquals(value, hashOperations.get(key, field));
        Assertions.assertNull(hashOperations.get(key, field1));
        jedisClient.del(key);
    }

    @Test
    void testMultiGet() {
        StandardHashOperations hashOperations = hashStringRedisTemplate.opsForHash();
        String key = "hash-test-mGet";
        Map<String, String>map = new HashMap<>();
        map.put("hash-test-mGet-field1", "hash-test-mGet-value1");
        map.put("hash-test-mGet-field2", "hash-test-mGet-value2");

        jedisClient.del(key);
        Assertions.assertArrayEquals(new String[]{null, null}, hashOperations.multiGet(key, map.keySet()).toArray());

        jedisClient.hmset(key, map);
        Assertions.assertArrayEquals(map.values().toArray(), hashOperations.multiGet(key, map.keySet()).toArray());
        jedisClient.del(key);
    }

    @Test
    void testIncrement() {
        StandardHashOperations hashOperations = hashStringRedisTemplate.opsForHash();
        String key = "hash-test-increment";
        String field1 = "field1";
        String field2 = "field2";
        jedisClient.del(key);
        jedisClient.hset(key, field1, "-2");
        jedisClient.hset(key, field2, "0.5");
        // increment(K key, HK hashKey, long delta)
        hashOperations.increment(key, field1, 2L);
        Assertions.assertEquals(0L, Long.valueOf(jedisClient.hget(key, field1)));
        // increment(K key, HK hashKey, double delta)
        hashOperations.increment(key, field2, 2.4);
        Assertions.assertEquals(2.9, Double.valueOf(jedisClient.hget(key, field2)));
    }

    @Test
    void testRandomKey() {

        StandardHashOperations hashOperations = hashStringRedisTemplate.opsForHash();
        String key = "test-random-key";
        Map<String, String> hash = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            String field = generateRandomString.apply(30);
            String value = generateRandomString.apply(100);
            hash.put(field, value);
        }
        jedisClient.hmset(key, hash);

        // randomKey(String key)
        String hKey = (String) hashOperations.randomKey(key);
        Assertions.assertTrue(hash.containsKey(hKey));

        // List<HK> randomKeys(K key, long count)
        List<String>hKeys = hashOperations.randomKeys(hKey, 30);

        Assertions.assertTrue(() -> {
            for (String hkey : hKeys) {
                if (!hash.containsKey(hkey)) {
                    return false;
                }
            }
            return true;
        });
        // keys
        Set<String> keys = hashOperations.keys(key);
        // AbstractSet 的 equals
        Assertions.assertEquals(keys, hash.keySet());

        jedisClient.hdel(key, hash.keySet().toArray(new String[0]));
    }

    @Test
    void testRandomEntry() {

        StandardHashOperations hashOperations = hashStringRedisTemplate.opsForHash();
        String key = "test-random-key";
        Map<String, String> hash = generateRandomStringMap.apply(100);

        jedisClient.hmset(key, hash);

        // randomEntry
        for (int i = 0; i < 30; i++) {
            Map.Entry<String, String> entry = hashOperations.randomEntry(key);
            Assertions.assertEquals(hash.get(entry.getKey()), entry.getValue());
        }

        // randomEntries
        Map<String, String> entries = hashOperations.randomEntries(key, 10);
        entries.entrySet().forEach(entry -> {
            Assertions.assertEquals(hash.get(entry.getKey()), entry.getValue());
        });

        jedisClient.hdel(key, hash.keySet().toArray(new String[0]));
    }

    @Test
    void testSize() {
        Random random = new Random();
        int size = 100 + random.nextInt(1000);
        StandardHashOperations hashOperations = hashStringRedisTemplate.opsForHash();
        String key = "hash-test-size";

        Map<String, String> hash = generateRandomStringMap.apply(size);
        jedisClient.del(key);
        jedisClient.hmset(key, hash);
        Assertions.assertEquals(hash.size(), hashOperations.size(key));
        jedisClient.del(key);
    }

    @Test
    void testLongOfValues() {
        // Long lengthOfValue(K key, HK hashKey) -> strLen(value)
        StandardHashOperations hashOperations = hashStringRedisTemplate.opsForHash();
        Map<String, String> hash = generateRandomStringMap.apply(200);
        String key = "hash-testLongOfValues";
        jedisClient.del(key);
        jedisClient.hmset(key, hash);
        hash.entrySet().forEach(entry -> {
            String field = entry.getKey();
            String value = entry.getValue();
            Assertions.assertEquals(value.length(), hashOperations.lengthOfValue(key, field));
        });
    }

    @Test
    void testPut() {
        StandardHashOperations hashOperations = hashStringRedisTemplate.opsForHash();
        String key = "hash-test-put";
        jedisClient.del(key);
        Map<String, String> hash = generateRandomStringMap.apply(200);
        hash.entrySet().forEach(entry -> {
            String field = entry.getKey();
            String value = entry.getValue();
            Assertions.assertNull(jedisClient.hget(key, field));
            hashOperations.put(key, field, value);
            Assertions.assertEquals(value, jedisClient.hget(key, field));
        });
        jedisClient.del(key);
    }

    @Test
    void testPutAll() {
        StandardHashOperations hashOperations = hashStringRedisTemplate.opsForHash();
        String key = "hash-test-putAll";
        jedisClient.del(key);
        Map<String, String> hash = generateRandomStringMap.apply(200);
        hashOperations.putAll(key, hash);
        hash.entrySet().forEach(entry -> {
            String field = entry.getKey();
            String value = entry.getValue();
            Assertions.assertEquals(value, jedisClient.hget(key, field));
        });
        jedisClient.del(key);
    }

    @Test
    void testPutIfAbsent() {
        StandardHashOperations hashOperations = hashStringRedisTemplate.opsForHash();
        String key = "hash-test-putAll";
        jedisClient.del(key);
        Map<String, String> hash = generateRandomStringMap.apply(100);
        // 旧值先set进去
        jedisClient.hmset(key, hash);
        Map<String, String> newHash = generateRandomStringMap.apply(100);

        // 加上所有老的
        newHash.putAll(hash);
        // 修改全部的字符串值
        newHash = newHash.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue() + "new",
                        (oldValue, newValue) -> newValue,
                        HashMap::new
                ));
        newHash.entrySet().forEach(entry -> {
            String field = entry.getKey();
            String value = entry.getValue();
            Boolean res = hashOperations.putIfAbsent(key, field, value);
            String ans = jedisClient.hget(key, field);
            if (hash.containsKey(field)) {
                Assertions.assertFalse(res);
                Assertions.assertEquals(hash.get(field), ans);
                Assertions.assertNotEquals(value, ans);
            } else {
                Assertions.assertTrue(res);
                Assertions.assertEquals(value, ans);
            }
        });

        jedisClient.del(key);
    }

    @Test
    void testValues() {
        StandardHashOperations hashOperations = hashStringRedisTemplate.opsForHash();
        String key = "hash-test-Values";
        jedisClient.del(key);
        Map<String, String> hash = generateRandomStringMap.apply(200);
        jedisClient.hmset(key, hash);

        List<String> values = hashOperations.values(key);
        Assertions.assertEquals(values.size(), hash.size());

        String[] valueArray = values.toArray(new String[0]);
        Arrays.sort(valueArray);

        String[] expected = hash.values().toArray(new String[0]);
        Arrays.sort(expected);

        Assertions.assertArrayEquals(expected, valueArray);
        jedisClient.del(key);
    }

    @Test
    void testDelete() {
        StandardHashOperations hashOperations = hashStringRedisTemplate.opsForHash();

        String key = "hash-test-delete";
        Map<String, String> map = generateRandomStringMap.apply(200);
        jedisClient.del(key);
        jedisClient.hmset(key, map);

        map.entrySet().forEach(entry -> {
            Assertions.assertNotNull(jedisClient.hget(key, entry.getKey()));
            String field = entry.getKey();
            Long isDelete = hashOperations.delete(key, entry.getKey());
            Assertions.assertEquals(1L, isDelete);
            Assertions.assertNull(jedisClient.hget(key, entry.getKey()));
            isDelete = hashOperations.delete(key, entry.getKey());
            Assertions.assertEquals(0L, isDelete);
        });

        jedisClient.del(key);
    }

    @Test
    void testEntries() {
        StandardHashOperations hashOperations = hashStringRedisTemplate.opsForHash();
        String key = "hash-test-putAll";
        jedisClient.del(key);
        Map<String, String> hash = generateRandomStringMap.apply(200);
        jedisClient.hmset(key, hash);

        /**
         * AbstractMap 也重写了equals
         */
        Map<String, String> entries = hashOperations.entries(key);

        Assertions.assertEquals(entries, hash);
        jedisClient.del(key);
    }

    @Test
    void testGetOperations() {
        StandardHashOperations hashOperations = hashStringRedisTemplate.opsForHash();
        Assertions.assertEquals(hashStringRedisTemplate, hashOperations.getOperations());
    }
}
