package com.easycode8.easylog.autoconfigure;

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
    @ConditionalOnProperty(value = "spring.easy-log.swagger.enabled", havingValue = "true")
    static class SwaggerSource {

        @Bean
        public LogAttributeSource logAttributeSource() {
            return new SwaggerLogAttributeSource();
        }

    }

    @ConditionalOnMissingBean(LogAttributeSource.class)
    static class EasyLogSource {
        @Bean
        public LogAttributeSource logAttributeSource() {
            return new AnnotationLogAttributeSource();
        }

    }

}
