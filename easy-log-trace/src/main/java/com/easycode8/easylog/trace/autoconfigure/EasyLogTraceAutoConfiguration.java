package com.easycode8.easylog.trace.autoconfigure;

import com.easycode8.easylog.autoconfigure.EasyLogAutoConfiguration;
import com.easycode8.easylog.core.trace.LogTracer;
import com.easycode8.easylog.trace.LogTraceConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@ConditionalOnBean(TraceAutoConfiguration.class)
@AutoConfigureAfter({TraceAutoConfiguration.class})
@AutoConfigureBefore({EasyLogAutoConfiguration.class})
@ConditionalOnProperty(value = "spring.easy-log.trace.enabled", havingValue = "true", matchIfMissing = true)
public class EasyLogTraceAutoConfiguration {


    /**按优先级选择链路追踪实现 zipkin>local*/
    @Configuration
    @ConditionalOnMissingBean(LogTracer.class)
    @ConditionalOnProperty(
            value = {"spring.sleuth.enabled", "spring.zipkin.enabled"},
            matchIfMissing = true
    )
    @Import({LogTraceConfiguration.ZipKinTranceConfig.class, LogTraceConfiguration.DefaultTraceConfig.class})
    static class ChooseLogTraceConfiguration {

    }
}
