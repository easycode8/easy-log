package com.easycode8.easylog.core.aop.interceptor;

import com.easycode8.easylog.core.util.LogUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCacheLogAttributeSource implements LogAttributeSource{

    private ConcurrentHashMap<String, LogAttribute> cacheMap = new ConcurrentHashMap<>();
    @Override
    public LogAttribute getLogAttribute(Method method, Class<?> targetClass) {
        String key = LogUtils.createDefaultTitle(method, targetClass, false);
        if (cacheMap.containsKey(key)) {
            return cacheMap.get(key);
        }
        LogAttribute attribute = doGetLogAttribute(method, targetClass);
        if (attribute != null) {
            cacheMap.put(key, attribute);
        }
        return attribute;
    }

    public abstract LogAttribute doGetLogAttribute(Method method, Class<?> targetClass);

    public ConcurrentHashMap<String, LogAttribute> getCacheMap() {
        return cacheMap;
    }
}
