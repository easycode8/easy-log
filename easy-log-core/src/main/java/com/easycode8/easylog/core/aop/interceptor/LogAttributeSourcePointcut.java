package com.easycode8.easylog.core.aop.interceptor;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.lang.reflect.Method;

public abstract class LogAttributeSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {

    /**
     * 是否作为切点的判断--通过被增强的方法和目标类是否能够提取出日志属性。
     * 将是否能够提取出日志属性能力通过接口暴露出去
     * @param method
     * @param targetClass
     * @return
     */
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        LogAttributeSource source = getLogAttributeSource();
        return (source == null || source.getLogAttribute(method, targetClass) != null);
    }

    @Nullable
    protected abstract LogAttributeSource getLogAttributeSource();
}
