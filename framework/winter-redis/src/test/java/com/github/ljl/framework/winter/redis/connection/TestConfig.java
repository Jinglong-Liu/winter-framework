package com.github.ljl.framework.winter.redis.connection;

import org.junit.jupiter.api.Assertions;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-17 21:53
 **/

class TestConfig {
    static String _characters = "qwertyuiop[\\]asdfghjkl;'zxcvbnm,./12asdaskpjo;lsf34567890-=!@#$%^&*()_";
    static Random _random = new Random();
    static Function<Integer, String> generateRandomString = (length -> {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = _random.nextInt(_characters.length());
            sb.append(_characters.charAt(index));
        }
        return sb.toString();
    });
    static Function<Integer, Map<String, String>> generateRandomStringMap = (size -> {
        Map<String, String> hash = new HashMap<>();
        while(hash.size() < size) {
            String field = generateRandomString.apply(_random.nextInt(100));
            String value = generateRandomString.apply(_random.nextInt(200));
            hash.put(field, value);
        }
        Assertions.assertEquals(size, hash.size());
        return hash;
    });

    static BiFunction<Integer, Integer, List<Integer>> randomIntGenerate = ((size, max) -> {
        List<Integer> list = new ArrayList<>();
        IntStream.range(0, size).forEach(index -> {
            list.add(_random.nextInt(max));
        });
        return list;
    });

    static Function<Integer, Set<String>> generateRandomStringSet = (size) -> {
        return generateRandomStringMap.apply(size).keySet();
    };
    static BiConsumer<Jedis, Collection<String>> clearKeys = (jedis, collection) -> {
        jedis.del(collection.toArray(new String[0]));
    };
}


