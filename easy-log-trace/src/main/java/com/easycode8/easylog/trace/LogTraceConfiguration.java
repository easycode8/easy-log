package com.easycode8.easylog.trace;

import brave.Tracer;
import com.easycode8.easylog.core.trace.LogTracer;
import com.easycode8.easylog.trace.filter.EasyLogTraceFilter;
import com.easycode8.easylog.trace.filter.MDCConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

public abstract class LogTraceConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogTraceConfiguration.class);

    @ConditionalOnMissingBean(LogTracer.class)
    @ConditionalOnProperty(value = "spring.easy-log.trace.default.enabled", havingValue = "true", matchIfMissing = true)
    public static class DefaultTraceConfig {

        @Bean
        public LogTracer logTracer() {
            LOGGER.info("[easy-log]日志链路启用默认记录:{}", MDCConstants.TRACE_ID);
            return new DefaultLogTracer();
        }

        @Bean
        @ConditionalOnMissingBean(name = "easyLogTraceFilter")
        public FilterRegistrationBean<EasyLogTraceFilter> easyLogTraceFilter() {
            FilterRegistrationBean<EasyLogTraceFilter> registrationBean = new FilterRegistrationBean<>();
            registrationBean.setFilter(new EasyLogTraceFilter());
            registrationBean.addUrlPatterns("/*");
            registrationBean.setOrder(1);
            registrationBean.setName("easyLogTraceFilter[日志链路]");
            return registrationBean;
        }
    }

    @ConditionalOnProperty(
            value = {"spring.sleuth.enabled", "spring.zipkin.enabled", "spring.easy-log.trace.zipkin.enabled"},
            matchIfMissing = true
    )
    @ConditionalOnClass(Tracer.class)
    public static class ZipKinTranceConfig {

        @Bean
        @ConditionalOnBean(Tracer.class)
        @ConditionalOnMissingBean
        public LogTracer logTracer(Tracer tracer) {
            LOGGER.info("[easy-log]日志链路启用zipkin记录");
            return new ZipkinLogTracer(tracer);
        }
    }
}
