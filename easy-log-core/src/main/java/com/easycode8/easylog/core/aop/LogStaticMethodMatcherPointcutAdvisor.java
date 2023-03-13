package com.easycode8.easylog.core.aop;

import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

import java.lang.reflect.Method;

/**
 * TODO 暂未使用
 */
public class LogStaticMethodMatcherPointcutAdvisor extends StaticMethodMatcherPointcutAdvisor {

    @Override
    public boolean matches(Method method, Class<?> aClass) {
        return false;
    }
}
