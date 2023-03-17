package com.easycode8.easylog.autoconfigure.source;


import com.easycode8.easylog.core.aop.interceptor.LogAttributeSource;
import com.easycode8.easylog.core.aop.interceptor.LogAttribute;
import io.swagger.annotations.ApiOperation;

import java.lang.reflect.Method;

public class SwaggerLogAttributeSource implements LogAttributeSource {
    @Override
    public LogAttribute getLogAttribute(Method method, Class<?> targetClass) {
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        if (apiOperation == null) {
            return null;
        }
        LogAttribute logAttribute = new LogAttribute() {
            @Override
            public String title() {
                return apiOperation.value();
            }

            @Override
            public String handler() {
                return null;
            }

            @Override
            public String template() {
                return apiOperation.notes();
            }

            @Override
            public String operator() {
                return null;
            }
        };
        return logAttribute;
    }
}
