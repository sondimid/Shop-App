package com.example.ShopApp_BE.Service;


import java.util.Map;

public interface RedisService<K, F, V> {
    void set(K key, V value);

    Boolean hasKey(K key);

    V get(K key);

    void del(K key);

    void setTimeToLive(K key, long timeToLive);

    void hashSet(K key,F field, V value);

    V hashGet(K key, F field);

    Boolean hashHasKey(K key, F field);

    void hashIncrement(K key, F field, long value);

    void hashDecrement(K key, F field, long value);

    Map<F, V> hashGet(K key);

}
