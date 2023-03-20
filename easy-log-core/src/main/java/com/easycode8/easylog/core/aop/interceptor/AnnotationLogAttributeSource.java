package com.easycode8.easylog.core.aop.interceptor;

import com.easycode8.easylog.core.annotation.EasyLog;
import com.easycode8.easylog.core.annotation.EasyLogProperties;
import com.easycode8.easylog.core.util.LogUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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
                    String title = StringUtils.isEmpty(easyLog.title()) ? easyLog.value() : easyLog.title();
                    // 如果都没有定义标题使用默认标题
                    if (StringUtils.isEmpty(title)) {
                        title = LogUtils.createDefaultTitle(method, targetClass);
                    }
                    return title;
                }

                @Override
                public String handler() {
                    return easyLog.handler();
                }

                @Override
                public String template() {
                    return easyLog.template();
                }

                @Override
                public String operator() {
                    return null;
                }
            };
        }
        // 如果找不到EasyLog 检查是否开启server-debug模式
        if (isServicePublicMethod(method, targetClass)) {
            String title = LogUtils.createDefaultTitle(method, targetClass);

            return buildLogAttribute( title );
        }

        return null;
    }



    private boolean isServicePublicMethod(Method method, Class<?> targetClass) {
        return easyLogProperties.getScanService().getEnabled() && targetClass.getAnnotation(Service.class) != null
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

            @Override
            public String operator() {
                return null;
            }
        };
    }
}
