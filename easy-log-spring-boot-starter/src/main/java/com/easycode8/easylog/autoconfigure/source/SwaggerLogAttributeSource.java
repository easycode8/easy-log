package com.easycode8.easylog.autoconfigure.source;


import com.easycode8.easylog.core.annotation.EasyLog;
import com.easycode8.easylog.core.annotation.EasyLogProperties;
import com.easycode8.easylog.core.aop.interceptor.AbstractCacheLogAttributeSource;
import com.easycode8.easylog.core.aop.interceptor.DefaultLogAttribute;
import com.easycode8.easylog.core.aop.interceptor.LogAttribute;
import com.easycode8.easylog.core.aop.interceptor.LogAttributeSource;
import com.easycode8.easylog.core.cache.LogAttributeCache;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class SwaggerLogAttributeSource extends AbstractCacheLogAttributeSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerLogAttributeSource.class);
    final private LogAttributeSource logAttributeSource;


    public SwaggerLogAttributeSource(LogAttributeCache logAttributeCache, LogAttributeSource logAttributeSource, EasyLogProperties easyLogProperties) {
        super(logAttributeCache, easyLogProperties);
        LOGGER.info("[easy-log]启动Swagger日志增强");
        this.logAttributeSource = logAttributeSource;
    }

    @Override
    public LogAttribute doGetLogAttribute(Method method, Class<?> targetClass) {
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        if (apiOperation != null && method.getAnnotation(EasyLog.class) == null) {
            LogAttribute logAttribute = DefaultLogAttribute.builder()
                    .title(apiOperation.value())
                    //.template(apiOperation.notes()) //不适用note作为模板解析,因为可能会报错
                    .async(easyLogProperties.getAsync())
                    .build();
            return logAttribute;
        }


        return logAttributeSource.getLogAttribute(method, targetClass);
    }
}
