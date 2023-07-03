package com.easycode8.easylog.core.handler;

import com.easycode8.easylog.core.LogInfo;
import com.easycode8.easylog.core.aop.interceptor.LogAttribute;

import java.lang.reflect.Method;

/**
 * 日志处理器接口
 * @param <T>
 */
public interface LogDataHandler<T extends LogInfo> {

    /**
     * 初始化日志对象
     * @param logAttribute
     * @param method
     * @param targetClass
     * @return
     */
    T init(LogAttribute logAttribute, Method method,  Object[] args, Class<?> targetClass);

    /**
     * 执行方法前的日志处理
     * @param info
     * @param method
     * @param args
     * @param targetClass
     * @param targetObject
     */
    void before(T info, Method method, Object[] args, Class<?> targetClass, Object targetObject);

    /**
     * 执行方法后的日志处理
     * @param info
     * @param method
     * @param targetClass
     * @param returnValue
     */
    void after(T info, Method method, Class<?> targetClass, Object returnValue);
}
