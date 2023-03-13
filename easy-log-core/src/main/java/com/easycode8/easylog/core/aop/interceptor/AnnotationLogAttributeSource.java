package com.easycode8.easylog.core.aop.interceptor;

import com.easycode8.easylog.core.annotation.EasyLog;

import java.lang.reflect.Method;

public class AnnotationLogAttributeSource implements LogAttributeSource {
    @Override
    public LogAttribute getLogAttribute(Method method, Class<?> targetClass) {
        EasyLog easyLog = method.getAnnotation(EasyLog.class);
        if (easyLog == null) {
            return null;
        }
        LogAttribute logAttribute = new LogAttribute() {
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
        return logAttribute;
    }
}
