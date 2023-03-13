package com.easycode8.easylog.core.aop.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;

/**
 * MethodInterceptor org.aopalliance.aop.Advice
 */
public class LogMethodInterceptor extends LogAspectSupport implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        return invoke(invocation.getMethod(), targetClass, invocation.getArguments(), invocation::proceed);
    }
}
