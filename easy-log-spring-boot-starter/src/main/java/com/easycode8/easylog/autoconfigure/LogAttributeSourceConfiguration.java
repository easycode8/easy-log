package com.easycode8.easylog.autoconfigure;

import com.easycode8.easylog.core.annotation.EasyLogProperties;
import com.easycode8.easylog.core.aop.interceptor.LogAttributeSource;
import com.easycode8.easylog.autoconfigure.source.SwaggerLogAttributeSource;
import com.easycode8.easylog.core.aop.interceptor.AnnotationLogAttributeSource;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

abstract class LogAttributeSourceConfiguration {

    @ConditionalOnMissingBean(LogAttributeSource.class)
    @ConditionalOnClass(ApiOperation.class)
    @ConditionalOnProperty(value = "spring.easy-log.scan-swagger.enabled", havingValue = "true")
    static class SwaggerSource {

        @Bean
        public LogAttributeSource logAttributeSource(EasyLogProperties easyLogProperties) {
            return new SwaggerLogAttributeSource(new AnnotationLogAttributeSource(easyLogProperties));
        }

    }

    @ConditionalOnMissingBean(LogAttributeSource.class)
    static class EasyLogSource {
        @Bean
        public LogAttributeSource logAttributeSource(EasyLogProperties easyLogProperties) {
            return new AnnotationLogAttributeSource(easyLogProperties);
        }

    }

}
