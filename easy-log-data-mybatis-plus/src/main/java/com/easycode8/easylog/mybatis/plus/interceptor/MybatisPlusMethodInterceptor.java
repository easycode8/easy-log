package com.easycode8.easylog.mybatis.plus.interceptor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;

public class MybatisPlusMethodInterceptor extends DataLogAspectSupport implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 非BaseMapper类型跳过增强
        if (!(invocation.getThis() instanceof BaseMapper)) {
            return invocation.proceed();
        }
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
       return invoke(invocation.getThis(), targetClass, invocation.getMethod(), invocation.getArguments(), invocation::proceed);
    }
}
