package com.easycode8.easylog.core.cache;

import com.easycode8.easylog.core.aop.interceptor.LogAttribute;

import java.util.Map;

public interface LogAttributeCache {

    boolean containsKey(String key);
    void put(String key, LogAttribute attribute);
    LogAttribute get(String key);

    Map<String, LogAttribute> getAll();
}
