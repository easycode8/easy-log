package com.easycode8.easylog.core.aop.interceptor;


import com.easycode8.easylog.core.annotation.EasyLogProperties;
import com.easycode8.easylog.core.cache.LogAttributeCache;
import com.easycode8.easylog.core.util.LogUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public abstract class AbstractCacheLogAttributeSource implements LogAttributeSource {

    private final LogAttributeCache logAttributeCache;
    protected EasyLogProperties easyLogProperties;

    protected AbstractCacheLogAttributeSource(LogAttributeCache logAttributeCache, EasyLogProperties easyLogProperties) {
        this.logAttributeCache = logAttributeCache;
        this.easyLogProperties = easyLogProperties;
    }


    @Override
    public LogAttribute getLogAttribute(Method method, Class<?> targetClass) {
        String key;
        // 如果是代理对象获取代理接口的真实名称
        if (Proxy.isProxyClass(targetClass)) {
            key = LogUtils.createDefaultTitle(method, ((Class)targetClass.getGenericInterfaces()[0]), false);
        } else {
            key = LogUtils.createDefaultTitle(method, targetClass, false);
        }

        if (!easyLogProperties.getCache().getKeyPrefix().endsWith(":")) {
            key = easyLogProperties.getCache().getKeyPrefix() + ":" +  key;
        } else {
            key = easyLogProperties.getCache().getKeyPrefix() + key;
        }
        if (logAttributeCache.containsKey(key)) {
            return logAttributeCache.get(key);
        }
        LogAttribute attribute = doGetLogAttribute(method, targetClass);
        if (attribute != null) {
            logAttributeCache.put(key, attribute);
        }
        return attribute;
    }

    public abstract LogAttribute doGetLogAttribute(Method method, Class<?> targetClass);

    public Map<String, LogAttribute> getCacheMap() {
        return this.logAttributeCache.getAll();
    }

    /**
     * 更新日志属性缓存
     * @param key
     * @param logAttribute
     */
    public void updateCache(String key, LogAttribute logAttribute) {
        this.logAttributeCache.put(key, logAttribute);
    }
}
