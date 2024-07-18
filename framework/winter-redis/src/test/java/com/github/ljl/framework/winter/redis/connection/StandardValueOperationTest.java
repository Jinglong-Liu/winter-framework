package com.github.ljl.framework.winter.redis.connection;

import com.github.ljl.framework.winter.boot.WinterApplication;
import com.github.ljl.framework.winter.context.context.ConfigurableApplicationContext;
import com.github.ljl.framework.winter.redis.template.RedisTemplate;
import org.junit.jupiter.api.*;
import redis.clients.jedis.Jedis;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.ljl.framework.winter.redis.connection.TestConfig.*;

class StandardValueOperationTest {

    private static ConfigurableApplicationContext applicationContext;

    private static StandardValueOperation<String, String> valueOperations;

    private static Jedis jedis;

    private static RedisTemplate<String, String> stringStringRedisTemplate;

    private static Map<String, String> testMap;
    private static Consumer<Collection<String>> clearKeys = collection -> {
        jedis.del(collection.toArray(new String[0]));
    };
    private static Consumer<Map<String, String>> addAll = map -> {
        map.entrySet().forEach(entry -> {
            jedis.set(entry.getKey(), entry.getValue());
        });
    };

    @BeforeAll
    public static void init() {
        applicationContext = (ConfigurableApplicationContext) WinterApplication.run(StandardValueOperationTest.class);
        stringStringRedisTemplate = applicationContext.getBean("stringStringRedisTemplate", RedisTemplate.class);

        valueOperations = stringStringRedisTemplate.opsForValues();
        Assertions.assertNotNull(valueOperations);

        jedis = new Jedis("localhost", 6379);
        Assertions.assertEquals("PONG", jedis.ping());
    }
    @AfterAll
    public static void destroy() {
        applicationContext.close();
    }

    @AfterEach
    public void after() {
        if (Objects.nonNull(testMap)) {
            clearKeys.accept(testMap.keySet());
        }
    }


    @Test
    public void testSet() {
        testMap = generateRandomStringMap.apply(200);
        clearKeys.accept(testMap.keySet());

        testMap.entrySet().forEach(entry -> {
            Assertions.assertNull(jedis.get(entry.getKey()));
            valueOperations.set(entry.getKey(), entry.getValue());
            Assertions.assertEquals(entry.getValue(), jedis.get(entry.getKey()));
        });
    }
    @Test
    public void testSetTimeout() {
        final Map<String, String> map = generateRandomStringMap.apply(10);
        clearKeys.accept(map.keySet());
        Random random = new Random();
        for (Map.Entry<String, String> entry: map.entrySet()) {
            long timeout = random.nextInt(200);
            Assertions.assertNull(jedis.get(entry.getKey()));
            valueOperations.set(entry.getKey(), entry.getValue(), timeout, TimeUnit.MILLISECONDS);
            Assertions.assertEquals(entry.getValue(), jedis.get(entry.getKey()));
            try {
                Thread.sleep(TimeUnit.MILLISECONDS.toMillis(timeout + 100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Assertions.assertNull(jedis.get(entry.getKey()));
        }
        clearKeys.accept(map.keySet());
    }

    @Test
    public void testSetTimeoutDuration() {
        final Map<String, String> map = generateRandomStringMap.apply(10);
        clearKeys.accept(map.keySet());
        Random random = new Random();

        for (Map.Entry<String, String> entry: map.entrySet()) {
            long timeout = 10 + random.nextInt(200);
            final Duration duration = Duration.ofMillis(timeout);
            valueOperations.set(entry.getKey(), entry.getValue(), duration);
            Assertions.assertEquals(entry.getValue(), jedis.get(entry.getKey()));
            try {
                Thread.sleep(TimeUnit.MILLISECONDS.toMillis(timeout + 100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Assertions.assertNull(jedis.get(entry.getKey()));
        }

        clearKeys.accept(map.keySet());
    }
    @Test
    void testSetIfAbsent() {
        final Map<String, String> map = generateRandomStringMap.apply(10);
        clearKeys.accept(map.keySet());

        for (Map.Entry<String, String> entry: map.entrySet()) {
            Assertions.assertTrue(valueOperations.setIfAbsent(entry.getKey(), entry.getValue()));
            Assertions.assertFalse(valueOperations.setIfAbsent(entry.getKey(), entry.getValue()));
        }
        clearKeys.accept(map.keySet());
    }

    @Test
    void testSetIfAbsentTimeout() {
        final Map<String, String> map = generateRandomStringMap.apply(10);
        clearKeys.accept(map.keySet());
        Random random = new Random();

        for (Map.Entry<String, String> entry: map.entrySet()) {
            long timeout = 10 + random.nextInt(200);
            Assertions.assertNull(jedis.get(entry.getKey()));
            Assertions.assertTrue(valueOperations.setIfAbsent(entry.getKey(), entry.getValue(), timeout * 1000, TimeUnit.MICROSECONDS));
            Assertions.assertEquals(entry.getValue(), jedis.get(entry.getKey()));
            Assertions.assertFalse(valueOperations.setIfAbsent(entry.getKey(), entry.getValue(), timeout, TimeUnit.MILLISECONDS));
            try {
                Thread.sleep(TimeUnit.MILLISECONDS.toMillis(timeout + 10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Assertions.assertNull(jedis.get(entry.getKey()));
            Assertions.assertTrue(valueOperations.setIfAbsent(entry.getKey(), entry.getValue(), timeout, TimeUnit.MILLISECONDS));
        }

        clearKeys.accept(map.keySet());
    }

    @Test
    void testSetIfAbsentDuration() {
        final Map<String, String> map = generateRandomStringMap.apply(10);
        clearKeys.accept(map.keySet());
        Random random = new Random();

        for (Map.Entry<String, String> entry: map.entrySet()) {
            long timeout = 10 + random.nextInt(200);
            final Duration duration = Duration.ofMillis(timeout);
            Assertions.assertNull(jedis.get(entry.getKey()));
            Assertions.assertTrue(valueOperations.setIfAbsent(entry.getKey(), entry.getValue(), duration));
            Assertions.assertEquals(entry.getValue(), jedis.get(entry.getKey()));
            Assertions.assertFalse(valueOperations.setIfAbsent(entry.getKey(), entry.getValue(), duration));
            try {
                Thread.sleep(TimeUnit.MILLISECONDS.toMillis(timeout + 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Assertions.assertNull(jedis.get(entry.getKey()));
            Assertions.assertTrue(valueOperations.setIfAbsent(entry.getKey(), entry.getValue(), duration));
        }

        clearKeys.accept(map.keySet());
    }

    @Test
    void testSetIfPresent() {
        final Map<String, String> map = generateRandomStringMap.apply(10);
        clearKeys.accept(map.keySet());
        for (Map.Entry<String, String> entry: map.entrySet()) {
            Assertions.assertFalse(valueOperations.setIfPresent(entry.getKey(), entry.getValue()));
            Assertions.assertNull(jedis.get(entry.getKey()));
            jedis.set(entry.getKey(), entry.getKey() + "123456");
            Assertions.assertTrue(valueOperations.setIfPresent(entry.getKey(), entry.getValue()));
            Assertions.assertEquals(entry.getValue(), jedis.get(entry.getKey()));
        }
        clearKeys.accept(map.keySet());
    }

    /**
     *  TODO: 概率不过
     */
    @Test
    void testSetIfPresentTimeout() {
        final Map<String, String> map = generateRandomStringMap.apply(10);
        clearKeys.accept(map.keySet());
        Random random = new Random();

        for (Map.Entry<String, String> entry: map.entrySet()) {
            final long timeout = 10 + random.nextInt(200);
            Assertions.assertFalse(valueOperations.setIfPresent(entry.getKey(), entry.getValue(), timeout, TimeUnit.MILLISECONDS));
            Assertions.assertNull(jedis.get(entry.getKey()));
            jedis.set(entry.getKey(), entry.getValue());
            // set
            Assertions.assertTrue(valueOperations.setIfPresent(entry.getKey(), entry.getValue(), timeout, TimeUnit.MILLISECONDS));
            Assertions.assertEquals(entry.getValue(), jedis.get(entry.getKey()));
            try {
                Thread.sleep(TimeUnit.MILLISECONDS.toMillis(timeout + 100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Assertions.assertNull(jedis.get(entry.getKey()));
            Assertions.assertFalse(valueOperations.setIfPresent(entry.getKey(), entry.getValue(), timeout, TimeUnit.MILLISECONDS));
        }

        clearKeys.accept(map.keySet());
    }

    @Test
    void testSetIfPresentDuration() {
        final Map<String, String> map = generateRandomStringMap.apply(20);
        clearKeys.accept(map.keySet());
        Random random = new Random();

        for (Map.Entry<String, String> entry: map.entrySet()) {
            final long timeout = 10 + random.nextInt(200);
            final Duration duration = Duration.ofMillis(timeout);
            Assertions.assertFalse(valueOperations.setIfPresent(entry.getKey(), entry.getValue(), duration));
            Assertions.assertNull(jedis.get(entry.getKey()));
            jedis.set(entry.getKey(), entry.getValue());
            // set
            Assertions.assertTrue(valueOperations.setIfPresent(entry.getKey(), entry.getValue(), duration));
            Assertions.assertEquals(entry.getValue(), jedis.get(entry.getKey()));
            try {
                Thread.sleep(TimeUnit.MILLISECONDS.toMillis(timeout + 10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Assertions.assertNull(jedis.get(entry.getKey()));
            Assertions.assertFalse(valueOperations.setIfPresent(entry.getKey(), entry.getValue(), duration));
        }

        clearKeys.accept(map.keySet());
    }

    @Test
    void testGet() {
        final Map<String, String> map = generateRandomStringMap.apply(50);
        clearKeys.accept(map.keySet());

        for (Map.Entry<String, String> entry: map.entrySet()) {
            Assertions.assertNull(valueOperations.get(entry.getKey()));
            jedis.set(entry.getKey(), entry.getValue());
            Assertions.assertEquals(entry.getValue(), valueOperations.get(entry.getKey()));
        }
        clearKeys.accept(map.keySet());
    }

    @Test
    void testGetAndDelete() {
        final Map<String, String> map = generateRandomStringMap.apply(50);
        clearKeys.accept(map.keySet());

        for (Map.Entry<String, String> entry: map.entrySet()) {
            Assertions.assertNull(valueOperations.getAndDelete(entry.getKey()));
            jedis.set(entry.getKey(), entry.getValue());
            Assertions.assertEquals(entry.getValue(), valueOperations.getAndDelete(entry.getKey()));
            Assertions.assertNull(jedis.get(entry.getKey()));
        }
        clearKeys.accept(map.keySet());
    }

    @Test
    void testGetAndExpireTimeout() throws InterruptedException {
        final Map<String, String> map = generateRandomStringMap.apply(10);
        Random random = new Random();
        clearKeys.accept(map.keySet());
        for (Map.Entry<String, String> entry: map.entrySet()) {
            long timeout = 100 + random.nextInt(300);
            Assertions.assertNull(valueOperations.getAndExpire(entry.getKey(), timeout, TimeUnit.MILLISECONDS));
            jedis.set(entry.getKey(), entry.getValue());
            Assertions.assertEquals(entry.getValue(), valueOperations.getAndExpire(entry.getKey(), timeout, TimeUnit.MILLISECONDS));
            Thread.sleep(50);
            Assertions.assertEquals(entry.getValue(), valueOperations.getAndExpire(entry.getKey(), timeout, TimeUnit.MILLISECONDS));
            Thread.sleep(timeout - 50 + 1);
            Assertions.assertNotNull(valueOperations.getAndExpire(entry.getKey(), timeout, TimeUnit.MILLISECONDS));
        }
        clearKeys.accept(map.keySet());
    }

    @Test
    void testGetAndExpireDuration() throws InterruptedException {
        final Map<String, String> map = generateRandomStringMap.apply(10);
        Random random = new Random();
        clearKeys.accept(map.keySet());
        for (Map.Entry<String, String> entry: map.entrySet()) {
            long timeout = 100 + random.nextInt(300);
            Duration duration = Duration.ofMillis(timeout);
            Assertions.assertNull(valueOperations.getAndExpire(entry.getKey(), duration));
            jedis.set(entry.getKey(), entry.getValue());
            Assertions.assertEquals(entry.getValue(), valueOperations.getAndExpire(entry.getKey(), duration));
            Thread.sleep(50);
            Assertions.assertEquals(entry.getValue(), valueOperations.getAndExpire(entry.getKey(), duration));
            Thread.sleep(timeout - 50 + 1);
            Assertions.assertNotNull(valueOperations.getAndExpire(entry.getKey(), duration));
        }
        clearKeys.accept(map.keySet());
    }

    /**
     * get 并不使其不过期
     * @throws InterruptedException
     */
    @Test
    void testGetAndPersist() throws InterruptedException {
        final Map<String, String> map = generateRandomStringMap.apply(10);
        Random random = new Random();
        clearKeys.accept(map.keySet());
        for (Map.Entry<String, String> entry: map.entrySet()) {
            long timeout = 100 + random.nextInt(300);
            Assertions.assertNull(valueOperations.getAndPersist(entry.getKey()));
            jedis.setex(entry.getKey(), timeout, entry.getValue());
            Assertions.assertEquals(entry.getValue(), valueOperations.getAndPersist(entry.getKey()));
            Thread.sleep(timeout  + 100);
            Assertions.assertEquals(entry.getValue(), jedis.get(entry.getKey()));
        }
        clearKeys.accept(map.keySet());
    }

    @Test
    void testGetAndSet() {
        final Map<String, String> map = generateRandomStringMap.apply(10);
        clearKeys.accept(map.keySet());
        for (Map.Entry<String, String> entry: map.entrySet()) {
            Assertions.assertNull(valueOperations.getAndSet(entry.getKey(), entry.getValue()));
            jedis.set(entry.getKey(), entry.getValue());
            Assertions.assertEquals(entry.getValue(), valueOperations.getAndSet(entry.getKey(), entry.getKey()));
            Assertions.assertEquals(entry.getKey(), jedis.get(entry.getKey()));
        }
        clearKeys.accept(map.keySet());
    }

    @Test
    public void testMultiSet() {
        Map<String, String> map = generateRandomStringMap.apply(100);
        map.put("key1", "value1");
        map.put("key2", "value2");

        clearKeys.accept(map.keySet());

        valueOperations.multiSet(map);

        List<String> values = jedis.mget(map.keySet().toArray(new String[0]));

        String[] strings = values.toArray(new String[0]);

        Arrays.sort(strings);

        clearKeys.accept(map.keySet());

        String[] expected = new ArrayList<>(map.values()).toArray(new String[0]);
        Arrays.sort(expected);

        Assertions.assertArrayEquals(expected, strings);
    }

    @Test
    public void testMultiSetIfAbsent() {
        Map<String, String> map = generateRandomStringMap.apply(100);
        Map<String, String> subMap1 = map.entrySet().stream()
                .filter(entry -> entry.hashCode() % 2 == 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<String, String> subMap2 = map.entrySet().stream()
                .filter(entry -> entry.hashCode() % 2 == 1)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() + "123456"));

        clearKeys.accept(map.keySet());

        Assertions.assertTrue(valueOperations.multiSetIfAbsent(subMap1));

        Assertions.assertTrue(valueOperations.multiSetIfAbsent(subMap2));

        Assertions.assertFalse(valueOperations.multiSetIfAbsent(map));

        List<String> result = jedis.mget(new ArrayList<>(subMap2.keySet()).toArray(new String[0]));

        String[] expected = new ArrayList<String>(subMap2.values()).toArray(new String[0]);
        String[] actual = result.toArray(new String[0]);
        Arrays.sort(expected);
        Arrays.sort(actual);

        clearKeys.accept(map.keySet());

        Assertions.assertArrayEquals(expected, actual);

    }

    @Test
    public void testMultiGet() {
        Map<String, String> map = generateRandomStringMap.apply(100);

        clearKeys.accept(map.keySet());

        List<String> nullList = valueOperations.multiGet(map.keySet());
        for (String expectedNull: nullList) {
            Assertions.assertNull(expectedNull);
        }

        valueOperations.multiSet(map);

        List<String> result = valueOperations.multiGet(map.keySet());

        String[] expected = new ArrayList<>(map.values()).toArray(new String[0]);
        String[] actual = result.toArray(new String[0]);
        Arrays.sort(expected);
        Arrays.sort(actual);

        clearKeys.accept(map.keySet());

        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testIncrement() {
        String key = "incrementKey";
        jedis.set(key, "1");
        Long newValue = valueOperations.increment(key);
        assertEquals(2L, newValue);

        newValue = valueOperations.increment(key, 5);
        assertEquals(7L, newValue);

        Double doubleValue = valueOperations.increment(key, 2.5);
        assertEquals(9.5, doubleValue);

        valueOperations.getAndDelete("incrementKey");
        jedis.del(key);
    }

    @Test
    public void testDecrement() {
        String key = "decrementKey";
        valueOperations.set(key, "10");

        Long newValue = valueOperations.decrement(key);
        assertEquals(9L, newValue);

        newValue = valueOperations.decrement(key, 3);
        assertEquals(6L, newValue);

        valueOperations.getAndDelete(key);
    }

    @Test
    public void testAppend() {
        String key = "appendKey";
        valueOperations.set(key, "Hello");

        Integer length = valueOperations.append(key, " World");
        assertEquals(11, length);

        assertEquals("Hello World", valueOperations.get(key));

        valueOperations.getAndDelete(key);
    }






    @Test
    public void testGetRange() {
        String key = "rangeKey";
        valueOperations.set(key, "Hello World");

        String range = valueOperations.get(key, 0, 4);
        assertEquals("Hello", range);

        range = valueOperations.get(key, 6, 10);
        assertEquals("World", range);

        valueOperations.getAndDelete(key);
    }

    @Test
    public void testSetWithOffset() {
        String key = "offsetKey";
        jedis.set(key, "Hello World");
        valueOperations.set(key, "Redis", 6);

        assertEquals("Hello Redis", valueOperations.get(key));

        jedis.del(key);
    }

    @Test
    public void testSize() {
        String key = "sizeKey";
        jedis.set(key, "Hello");

        Long size = valueOperations.size(key);
        assertEquals(5, size);

        jedis.del(key);
    }

    @Test
    public void testSetBit() {
        String key = "bitKey";
        jedis.del(key);
        valueOperations.setBit(key, 1, true);
        valueOperations.setBit(key, 2, true);
        valueOperations.setBit(key, 3, false);

        assertTrue(valueOperations.getBit(key, 1));
        assertTrue(valueOperations.getBit(key, 2));
        assertFalse(valueOperations.getBit(key, 3));
        valueOperations.getAndDelete(key);
    }

    @Test
    public void testGetBit() {
        String key = "bitKey";
        valueOperations.setBit(key, 1, true);

        Boolean bit = valueOperations.getBit(key, 1);
        assertTrue(bit);

        bit = valueOperations.getBit(key, 0);
        assertFalse(bit);
        valueOperations.getAndDelete(key);
    }

    @Test
    public void testGetOperations() {
        assertEquals(stringStringRedisTemplate, valueOperations.getOperations());
    }
//
////    List<Long> bitField(K key, BitFieldSubCommands subCommands);

}