package com.easycode8.easylog.core.adapter;

import com.easycode8.easylog.core.aop.interceptor.LogAttribute;

import java.lang.reflect.Method;

public interface LogAttributeMappingAdapter {

    /** 预留排序接口,用于多个日志属性适配器命中多个接口增强时候定义优先级,*/
    default int order() {
        return 10;
    }

    LogAttribute getLogAttribute(Method method, Class<?> targetClass);
}
