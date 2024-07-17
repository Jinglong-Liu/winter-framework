package com.github.ljl.framework.winter.redis.connection;

import com.github.ljl.framework.winter.boot.WinterApplication;
import com.github.ljl.framework.winter.context.context.ConfigurableApplicationContext;
import com.github.ljl.framework.winter.redis.template.RedisTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


class StandardValueOperationTest {

    private static ConfigurableApplicationContext applicationContext;

    private static StandardValueOperation<String, String> valueOperations;
    @BeforeAll
    public static void init() {
        applicationContext = (ConfigurableApplicationContext) WinterApplication.run(StandardValueOperationTest.class);
        //redisTemplate = applicationContext.getBean(RedisTemplate.class);
        // redisTemplate = applicationContext.getBean("redisTemplate");
        RedisTemplate<String, String> stringStringRedisTemplate = applicationContext.getBean("stringStringRedisTemplate", RedisTemplate.class);

        valueOperations = stringStringRedisTemplate.opsForValues();
        Assertions.assertNotNull(valueOperations);
    }
    @AfterAll
    public static void destroy() {
        applicationContext.close();
    }

    @Test
    void testSetAndGet() {
        String key = "test-key";
        String value = "test-value";

        // 测试 set 和 get 方法
        valueOperations.set(key, value);
        String retrievedValue = (String) valueOperations.get(key);

        assertEquals(value, retrievedValue, "Expected and retrieved values should match");
    }

    @Test
    void testSetWithTimeout() throws InterruptedException {
        String key = "test-key-timeout";
        String value = "test-value-timeout";
        long timeout = 1; // 1秒

        // 测试带有超时的 set 方法
        valueOperations.set(key, value, timeout, TimeUnit.SECONDS);

        // 等待超时时间
        Thread.sleep(TimeUnit.SECONDS.toMillis(timeout + 1));

        String retrievedValue = (String) valueOperations.get(key);
        assertNull(retrievedValue, "Value should not exist after timeout");
    }

    @Test
    void testSetIfAbsent() {
        String key = "test-key-absent";
        String value = "test-value-absent";
        valueOperations.getAndDelete(key);
        // 测试 setIfAbsent 方法
        boolean setResult = valueOperations.setIfAbsent(key, value);
        assertTrue(setResult, "Expected setIfAbsent to succeed on first attempt");

        boolean setResultAgain = valueOperations.setIfAbsent(key, "new-value");
        assertFalse(setResultAgain, "Expected setIfAbsent to fail on second attempt");
        valueOperations.getAndDelete(key);
    }

    @Test
    void testGetAndSet() {
        String key = "test-key-get-set";
        String initialValue = "initial-value";
        String updatedValue = "updated-value";

        // 设置初始值
        valueOperations.set(key, initialValue);

        // 测试 getAndSet 方法
        String previousValue = (String) valueOperations.getAndSet(key, updatedValue);

        assertEquals(initialValue, previousValue, "Expected previous value to be initial-value");
        assertEquals(updatedValue, valueOperations.get(key), "Expected updated value after getAndSet");
    }

    @Test
    void testGetAndExpire() throws InterruptedException {
        String key = "test-key-expire";
        String value = "test-value-expire";
        long timeout = 1; // 1秒

        // 设置值并测试 getAndExpire 方法
        valueOperations.set(key, value);
        String retrievedValue = (String) valueOperations.getAndExpire(key, timeout, TimeUnit.SECONDS);

        assertEquals(value, retrievedValue, "Expected value before expiration");

        // 等待超时时间
        Thread.sleep(TimeUnit.SECONDS.toMillis(timeout + 1));

        String expiredValue = (String) valueOperations.get(key);
        assertNull(expiredValue, "Expected value to be expired and null");
    }

    @Test
    void testGetAndDelete() {
        String key = "test-key-delete";
        String value = "test-value-delete";

//        Jedis jedis = new Jedis("localhost",6379);
//        jedis.set(key,value);
//        jedis.getDel(key);
//        String val = jedis.get(key);
//        System.out.println(val);

        // 设置值并测试 getAndDelete 方法
        valueOperations.set(key, value);
        String deletedValue = (String) valueOperations.getAndDelete(key);

        assertEquals(value, deletedValue, "Expected deleted value to match set value");

        String retrievedValue = (String) valueOperations.get(key);
        assertNull(retrievedValue, "Expected value to be deleted and null");
    }

    @Test
    void testGetAndPersist() throws InterruptedException {
        String key = "test-key-persist";
        String value = "test-value-persist";

        // 设置值并测试 getAndPersist 方法
        valueOperations.set(key, value);
        valueOperations.getAndPersist(key); // 持久化操作，不影响值的验证

        // 等待持久化完成
        Thread.sleep(100); // 等待持久化操作完成，可以根据实际情况调整

        String retrievedValue = (String) valueOperations.get(key);
        assertEquals(value, retrievedValue, "Expected value to remain after persist operation");
    }

    @Test
    void testSetIfPresent() {
        String key = "test-key-present";
        String value = "test-value-present";
        String newValue = "new-value-present";

        // 设置初始值
        valueOperations.set(key, value);

        // 测试 setIfPresent 方法
        boolean setResult = valueOperations.setIfPresent(key, newValue);
        assertTrue(setResult, "Expected setIfPresent to succeed when key exists");

        String retrievedValue = (String) valueOperations.get(key);
        assertEquals(newValue, retrievedValue, "Expected value to be updated by setIfPresent");

        boolean setResultAgain = valueOperations.setIfPresent("nonexistent-key", "new-value");
        assertFalse(setResultAgain, "Expected setIfPresent to fail when key does not exist");
    }

    @Test
    public void testMultiSet() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        valueOperations.multiSet(map);

        assertEquals("value1", valueOperations.get("key1"));
        assertEquals("value2", valueOperations.get("key2"));

        valueOperations.getAndDelete("key1");
        valueOperations.getAndDelete("key2");
    }

    @Test
    public void testMultiSetIfAbsent() {
        Map<String, String> map = new HashMap<>();
        map.put("key3", "value3");
        map.put("key4", "value4");

        Boolean result = valueOperations.multiSetIfAbsent(map);

        assertTrue(result);
        assertEquals("value3", valueOperations.get("key3"));
        assertEquals("value4", valueOperations.get("key4"));

        // 尝试覆盖已存在的 key
        map.put("key3", "newValue3");
        result = valueOperations.multiSetIfAbsent(map);

        assertFalse(result);
        assertEquals("value3", valueOperations.get("key3")); // 值应未改变

        valueOperations.getAndDelete("key3");
        valueOperations.getAndDelete("key4");
    }

    @Test
    public void testMultiGet() {
        List<String> keys = Arrays.asList("key1", "key2", "key5");
        valueOperations.set("key1", "value1");
        valueOperations.set("key2", "value2");

        List<String> values = valueOperations.multiGet(keys);

        assertEquals(Arrays.asList("value1", "value2", null), values);
        valueOperations.getAndDelete("key1");
        valueOperations.getAndDelete("key2");
    }

    @Test
    public void testIncrement() {
        String key = "incrementKey";
        valueOperations.set(key, "1");

        Long newValue = valueOperations.increment(key);
        assertEquals(2L, newValue);

        newValue = valueOperations.increment(key, 5);
        assertEquals(7L, newValue);

        Double doubleValue = valueOperations.increment(key, 2.5);
        assertEquals(9.5, doubleValue);

        valueOperations.getAndDelete("incrementKey");
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
        valueOperations.set(key, "Hello World");
        valueOperations.set(key, "Redis", 6);

        assertEquals("Hello Redis", valueOperations.get(key));

        valueOperations.getAndDelete(key);
    }

    @Test
    public void testSize() {
        String key = "sizeKey";
        valueOperations.set(key, "Hello");

        Long size = valueOperations.size(key);
        assertEquals(5, size);

        valueOperations.getAndDelete(key);
    }

    @Test
    public void testSetBit() {
        String key = "bitKey";
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
}