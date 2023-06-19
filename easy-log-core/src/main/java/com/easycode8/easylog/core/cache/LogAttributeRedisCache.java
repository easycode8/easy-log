package com.easycode8.easylog.core.cache;

import com.alibaba.fastjson.JSON;
import com.easycode8.easylog.core.annotation.EasyLogProperties;
import com.easycode8.easylog.core.aop.interceptor.DefaultLogAttribute;
import com.easycode8.easylog.core.aop.interceptor.LogAttribute;
import org.springframework.data.redis.core.StringRedisTemplate;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LogAttributeRedisCache implements LogAttributeCache{

    private final StringRedisTemplate redisTemplate;
    private final EasyLogProperties easyLogProperties;

    public LogAttributeRedisCache(StringRedisTemplate redisTemplate, EasyLogProperties easyLogProperties) {
        this.redisTemplate = redisTemplate;
        this.easyLogProperties = easyLogProperties;
    }

    @Override
    public boolean containsKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void put(String key, LogAttribute attribute) {
        redisTemplate.opsForValue().set(key, JSON.toJSONString(attribute));
    }

    @Override
    public LogAttribute get(String key) {
        return JSON.parseObject(redisTemplate.opsForValue().get(key), DefaultLogAttribute.class);
    }

    @Override
    public Map<String, LogAttribute> getAll() {
        Set<String> keys= redisTemplate.keys(easyLogProperties.getCache().getKeyPrefix() + "*");
        Map<String,LogAttribute> map = new HashMap<>();
        for (String key : keys) {
            map.put(key, this.get(key));
        }

        return map;
    }
}
