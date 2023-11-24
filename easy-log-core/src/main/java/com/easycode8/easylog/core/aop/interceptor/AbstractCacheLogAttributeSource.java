package com.easycode8.easylog.core.aop.interceptor;


import com.easycode8.easylog.core.annotation.EasyLogProperties;
import com.easycode8.easylog.core.cache.LogAttributeCache;
import com.easycode8.easylog.core.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;

public abstract class AbstractCacheLogAttributeSource implements LogAttributeSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCacheLogAttributeSource.class);
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
            String className = ((Class) targetClass.getGenericInterfaces()[0]).getName();
            if (!this.matches(className, easyLogProperties.getScanPackages())) {
                return null;
            }
            key = LogUtils.createDefaultTitle(method, ((Class) targetClass.getGenericInterfaces()[0]), false);

        } else {
            if (!this.matches(targetClass.getName(), easyLogProperties.getScanPackages())) {
                return null;
            }
            if (targetClass.getAnnotation(SpringBootApplication.class) != null) {
                return null;
            }

            key = LogUtils.createDefaultTitle(method, targetClass, false);
        }

        if (!easyLogProperties.getCache().getKeyPrefix().endsWith(":")) {
            key = easyLogProperties.getCache().getKeyPrefix() + ":" + key;
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
     *
     * @param key
     * @param logAttribute
     */
    public void updateCache(String key, LogAttribute logAttribute) {
        this.logAttributeCache.put(key, logAttribute);
    }

    private boolean matches(String className, Set<String> basePackages) {
        if (basePackages == null || basePackages.isEmpty()) {
            return true;
        }
        boolean matches = basePackages.stream().anyMatch(item -> className.startsWith(item));
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("[ease-log] 类:{} AOP增强:{} 匹配前缀:{}", className, matches, basePackages);
        }
        return matches;
    }
}
