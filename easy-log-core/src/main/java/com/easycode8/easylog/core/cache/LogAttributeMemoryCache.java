package com.easycode8.easylog.core.cache;



import com.easycode8.easylog.core.aop.interceptor.LogAttribute;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogAttributeMemoryCache implements LogAttributeCache{

    private ConcurrentHashMap<String, LogAttribute> cacheMap = new ConcurrentHashMap<>();
    @Override
    public boolean containsKey(String key) {
        return cacheMap.containsKey(key);
    }

    @Override
    public void put(String key, LogAttribute attribute) {
        cacheMap.put(key, attribute);
    }

    @Override
    public LogAttribute get(String key) {
        return cacheMap.get(key);
    }

    @Override
    public Map<String, LogAttribute> getAll() {
        return cacheMap;
    }
}
