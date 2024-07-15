package com.github.ljl.framework.winter.webmvc.handler;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 21:25
 **/

public class MultiValueMap<K,V> implements Map<K, List<V>> {

    private Map<K, List<V>> map = new HashMap<>();

    @Override
    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        for (List<V> values: map.values()) {
            if (values.contains(values)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<V> get(Object key) {
        return map.get(key);
    }

    @Override
    public List<V> put(K key, List<V> value) {
        return map.put(key, value);
    }

    @Override
    public List<V> remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends List<V>> m) {
        m.forEach((key, values) -> {
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            List<V> list = map.get(key);
            list.addAll(values);
        });
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<List<V>> values() {
        return map.values();
    }

    @Override
    public Set<Map.Entry<K, List<V>>> entrySet() {
        return map.entrySet();
    }

    public void add(K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }
        map.get(key).add(value);
    }

    // Custom forEach method to iterate over (key, value) pairs
    public void forEachKV(BiConsumer<? super K, ? super V> action) {
        map.forEach((key, valueList) -> valueList.forEach(value -> action.accept(key, value)));
    }
}
