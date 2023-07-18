package com.easycode8.easylog.trace;

import brave.Tracer;
import com.easycode8.easylog.core.trace.LogTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

public abstract class LogTraceConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogTraceConfiguration.class);

    public static class DefaultTraceConfig {
        @Bean
        @ConditionalOnMissingBean
        public LogTracer logTracer() {
            LOGGER.info("[easy-log]日志链路使用默认记录(未实现)");
            return new DefaultLogTracer();
        }
    }

    @ConditionalOnClass(Tracer.class)
    @ConditionalOnProperty(value = "spring.easy-log.trace.zipkin.enabled", havingValue = "true", matchIfMissing = true)
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
