package com.easycode8.easylog.core.adapter;

import com.easycode8.easylog.core.annotation.EasyLogProperties;
import com.easycode8.easylog.core.aop.interceptor.DefaultLogAttribute;
import com.easycode8.easylog.core.aop.interceptor.LogAttribute;
import com.easycode8.easylog.core.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 从service bean日志属性提取
 */
public class ServiceLogAttributeMapping implements LogAttributeMappingAdapter{

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLogAttributeMapping.class);
    private final EasyLogProperties easyLogProperties;

    public ServiceLogAttributeMapping(EasyLogProperties easyLogProperties) {
        LOGGER.info("[easy-log]启动service bean日志增强");
        this.easyLogProperties = easyLogProperties;
    }

    @Override
    public LogAttribute getLogAttribute(Method method, Class<?> targetClass) {
        // 如果找不到EasyLog 检查是否开启server-debug模式
        if (isServicePublicMethod(method, targetClass)) {
            String title = LogUtils.createDefaultTitle(method, targetClass);

            return DefaultLogAttribute.builder()
                    .title(title)
                    .async(easyLogProperties.getAsync())
                    .build();
        }
        return null;
    }


    private boolean isServicePublicMethod(Method method, Class<?> targetClass) {
        // 如果方法来自于Object对象忽略处理
        if (method.getDeclaringClass().equals(Object.class)) {
            return false;
        }
        return easyLogProperties.getScanService().getEnabled() && targetClass.getAnnotation(Service.class) != null
                && !Modifier.isStatic(method.getModifiers())
                && Modifier.isPublic(method.getModifiers());
    }
}
