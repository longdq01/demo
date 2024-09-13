package com.example.producer.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setKeyExpireAt(String key, Object value, Date expireAt){
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expireAt(key, expireAt);
    }

    public boolean existKey(String key){
        Object value = redisTemplate.opsForValue().get(key);
        return value != null;
    }
}
