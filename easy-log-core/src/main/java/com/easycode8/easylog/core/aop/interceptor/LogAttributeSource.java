package com.easycode8.easylog.core.aop.interceptor;

import java.lang.reflect.Method;

public interface LogAttributeSource {
    LogAttribute getLogAttribute(Method method, Class<?> targetClass);
}
