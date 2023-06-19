package com.easycode8.easylog.autoconfigure;

import com.easycode8.easylog.core.adapter.LogAttributeMappingAdapter;
import com.easycode8.easylog.core.annotation.EasyLogProperties;
import com.easycode8.easylog.core.aop.interceptor.LogAttributeSource;
import com.easycode8.easylog.autoconfigure.source.SwaggerLogAttributeSource;
import com.easycode8.easylog.core.aop.interceptor.AnnotationLogAttributeSource;
import com.easycode8.easylog.core.cache.LogAttributeCache;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.util.List;

abstract class LogAttributeSourceConfiguration {

    @ConditionalOnMissingBean(LogAttributeSource.class)
    @ConditionalOnClass(ApiOperation.class)
    @ConditionalOnProperty(value = "spring.easy-log.scan-swagger.enabled", havingValue = "true")
    static class SwaggerSource {

        @Bean
        public LogAttributeSource logAttributeSource(LogAttributeCache logAttributeCache, EasyLogProperties easyLogProperties, List<LogAttributeMappingAdapter> mappingAdapters) {
            return new SwaggerLogAttributeSource(logAttributeCache, new AnnotationLogAttributeSource(logAttributeCache, easyLogProperties, mappingAdapters), easyLogProperties);
        }

    }

    @ConditionalOnMissingBean(LogAttributeSource.class)
    static class EasyLogSource {
        @Bean
        public LogAttributeSource logAttributeSource(LogAttributeCache logAttributeCache, EasyLogProperties easyLogProperties, List<LogAttributeMappingAdapter> mappingAdapters) {
            return new AnnotationLogAttributeSource(logAttributeCache, easyLogProperties, mappingAdapters);
        }

    }

}
