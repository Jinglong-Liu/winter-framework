package com.github.ljl.framework.winter.redis.connection;

import com.github.ljl.framework.winter.boot.WinterApplication;
import com.github.ljl.framework.winter.context.context.ConfigurableApplicationContext;
import com.github.ljl.framework.winter.redis.connection.bean.Info;
import com.github.ljl.framework.winter.redis.connection.bean.User;
import com.github.ljl.framework.winter.redis.template.RedisTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.ljl.framework.winter.redis.connection.TestConfig.*;

class StandardValueOperationTestForObject {

    private static ConfigurableApplicationContext applicationContext;

    private static Jedis jedis;

    private static RedisTemplate<String, Object> stringObjectRedisTemplate;

    private static RedisTemplate objectRedisTemplate;

    private static Map<String, String> testMap;

    private static Consumer<Map<String, String>> addAll = map -> {
        map.entrySet().forEach(entry -> {
            jedis.set(entry.getKey(), entry.getValue());
        });
    };

    @BeforeAll
    public static void init() {
        applicationContext = (ConfigurableApplicationContext) WinterApplication.run(StandardValueOperationTest.class);
        stringObjectRedisTemplate = applicationContext.getBean("stringObjectRedisTemplate", RedisTemplate.class);
        objectRedisTemplate = applicationContext.getBean("redisTemplate");

        jedis = new Jedis("localhost", 6379);
        Assertions.assertEquals("PONG", jedis.ping());
    }
    @AfterAll
    public static void destroy() {
        applicationContext.close();
    }

    @Test
    public void testSetObject() {
        StandardValueOperation valueOperations = stringObjectRedisTemplate.opsForValues();
        Set<User> userSet = randomUserSetGenerate.apply(100);
        Set<String> keySet = generateRandomStringSet.apply(100);
        clearKeys.accept(jedis, keySet);
        User[] users = userSet.toArray(new User[0]);
        String[] keys = keySet.toArray(new String[0]);
        for (int i = 0; i < 100; i++) {
            valueOperations.set(keys[i], users[i]);
            User actual = (User) valueOperations.get(keys[i]);
            Assertions.assertEquals(users[i], actual);
            jedis.del(keys[i]);
        }
        clearKeys.accept(jedis, keySet);
    }

    @Test
    public void testSetMap() {
        StandardValueOperation valueOperations = stringObjectRedisTemplate.opsForValues();
        Set<User> userSet = randomUserSetGenerate.apply(100);
        Set<String> keySet = generateRandomStringSet.apply(100);
        User[] users = userSet.toArray(new User[0]);
        String[] keys = keySet.toArray(new String[0]);
        Map<String, User> map = new HashMap<>();

        for (int i = 0; i < 100; i++) {
            map.put(keys[i], users[i]);
        }

        String key = "test-set-map";
        jedis.del(key);

        valueOperations.set(key, map);

        Map<String, User> actual = (Map<String, User>) valueOperations.get(key);

        Assertions.assertEquals(map, actual);

        jedis.del(key);
    }

    @Test
    public void testObjectKeyValue() {
        StandardValueOperation<Object, Object> valueOperation = objectRedisTemplate.opsForValues();

        Set<User> userSet = randomUserSetGenerate.apply(100);
        Set<Info> infoSet = randomInfoSetGenerate.apply(100);
        User[] users = userSet.toArray(new User[0]);
        Info[] infos = infoSet.toArray(new Info[0]);
        for (int i = 0; i < 100; i++) {
            valueOperation.setIfAbsent(users[i], infos[i]);
            Info actual = (Info) valueOperation.getAndDelete(users[i]);
            Assertions.assertEquals(infos[i], actual);
        }
        Assertions.assertNull(valueOperation.getAndPersist(users[0]));
    }

    private Supplier<User> randomUserGenerate = () -> {
        Integer id = _random.nextInt(10000);
        Integer age = 10 + _random.nextInt(50);
        String username = generateRandomString.apply(30);
        String email = generateRandomString.apply(50);
        return new User(id, username, email, age);
    };

    private Function<Integer, List<User>> randomUserListGenerate = (size) -> {
        List<User> users = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            users.add(randomUserGenerate.get());
        }
        return users;
    };

    private Function<Integer, Set<User>> randomUserSetGenerate = (size) -> {
        Set<User> users = new LinkedHashSet<>(size);
        while(users.size() < size) {
            users.add(randomUserGenerate.get());
        }
        return users;
    };

    private Supplier<Info> randomInfoGenerate = () -> {
        Integer integer1 = _random.nextInt(10000);
        Long long1 = _random.nextLong();
        String string1 = generateRandomString.apply(30);
        String string2 = generateRandomString.apply(50);
        return new Info(string1, long1, integer1, string2);
    };

    private Function<Integer, Set<Info>> randomInfoSetGenerate = (size) -> {
        Set<Info> infos = new LinkedHashSet<>(size);
        while(infos.size() < size) {
            infos.add(randomInfoGenerate.get());
        }
        return infos;
    };
}