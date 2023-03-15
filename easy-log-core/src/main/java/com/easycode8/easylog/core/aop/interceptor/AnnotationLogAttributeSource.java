package com.easycode8.easylog.core.aop.interceptor;

import com.easycode8.easylog.core.annotation.EasyLog;
import com.easycode8.easylog.core.annotation.EasyLogProperties;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AnnotationLogAttributeSource implements LogAttributeSource {

    private final EasyLogProperties easyLogProperties;

    public AnnotationLogAttributeSource(EasyLogProperties easyLogProperties) {
        this.easyLogProperties = easyLogProperties;
    }


    @Override
    public LogAttribute getLogAttribute(Method method, Class<?> targetClass) {
        EasyLog easyLog = method.getAnnotation(EasyLog.class);
        // EasyLog注解优先级最高
        if (easyLog != null) {
            return new LogAttribute() {
                @Override
                public String title() {
                    return easyLog.value();
                }

                @Override
                public String handler() {
                    return easyLog.handler();
                }

                @Override
                public String template() {
                    return easyLog.template();
                }
            };
        }
        // 如果找不到EasyLog 检查是否开启server-debug模式
        if (isServicePublicMethod(method, targetClass)) {
            String title = targetClass.getSimpleName() + "." + method.getName();
            if (method.getParameters() != null) {
                List<String> paramNames = Arrays.stream(method.getParameters()).map(item -> item.getName()).collect(Collectors.toList());
                Strings.join(paramNames, ',');
                title = title + "(" + Strings.join(paramNames, ',') + ")";
            } else {
                title = title + "()";
            }

            return buildLogAttribute( title );
        }

        return null;
    }

    private boolean isServicePublicMethod(Method method, Class<?> targetClass) {
        return easyLogProperties.getServiceDebug() && targetClass.getAnnotation(Service.class) != null
                && !Modifier.isStatic(method.getModifiers())
                && Modifier.isPublic(method.getModifiers());
    }

    private LogAttribute buildLogAttribute(String title) {
        return new LogAttribute() {
            @Override
            public String title() {
                return title;
            }

            @Override
            public String handler() {
                return "";
            }

            @Override
            public String template() {
                return "";
            }
        };
    }
}
