package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.Service.RedisService;
import com.example.ShopApp_BE.Utils.JwtTokenUtils;
import com.example.ShopApp_BE.Utils.TokenType;
import lombok.RequiredArgsConstructor;


import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl<K, F, V> implements RedisService<K, F, V> {
    private final RedisTemplate<K, V> redisTemplate;
    private final HashOperations<K, F, V> hashOperations;
    private static final int LIMIT_FORGOT_PASSWORD = 5;
    private static final int LIMIT_TIME = 1;
    private static final String FORGOT_PASSWORD_HASH = "forgot_password_hash";

    @Override
    public void set(K key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Boolean hasKey(K key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public V get(K key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void del(K key) {
        redisTemplate.delete(key);
    }

    @Override
    public void setTimeToLive(K key, long timeToLive) {
        redisTemplate.expire(key, timeToLive, TimeUnit.MILLISECONDS);
    }

    @Override
    public void hashSet(K key, F field, V value) {
        hashOperations.put(key, field, value);
    }

    @Override
    public V hashGet(K key, F field) {
        return hashOperations.get(key, field);
    }

    @Override
    public Boolean hashHasKey(K key, F field) {
        return hashOperations.hasKey(key, field);
    }

    @Override
    public void hashIncrement(K key, F field, long value) {
        redisTemplate.opsForHash().increment(key, field, value);
    }

    @Override
    public void hashDecrement(K key, F field, long value) {
        redisTemplate.opsForHash().increment(key, field, -value);
    }

    @Override
    public Map<F, V> hashGet(K key) {
        return (Map<F, V>) redisTemplate.opsForHash().entries(key);
    }

    @Override
    public Boolean isAllowedForgotPassword(String username) {
        Long currentReq = redisTemplate.opsForValue().increment((K) (FORGOT_PASSWORD_HASH + username));
        if(currentReq == 1){
            redisTemplate.expire((K) (FORGOT_PASSWORD_HASH + username), LIMIT_TIME, TimeUnit.SECONDS);
        }
        return currentReq <= LIMIT_FORGOT_PASSWORD;
    }
}
