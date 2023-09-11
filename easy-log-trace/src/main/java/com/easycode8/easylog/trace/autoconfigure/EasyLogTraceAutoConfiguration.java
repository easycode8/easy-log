package com.easycode8.easylog.trace.autoconfigure;

import com.easycode8.easylog.core.trace.LogTracer;
import com.easycode8.easylog.trace.LogTraceConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@AutoConfigureAfter(name = "org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration")
//@AutoConfigureBefore({EasyLogAutoConfiguration.class}) // 日志追踪者需要在日志核心框架执行初始化 挪到starter after等效
@ConditionalOnProperty(value = {"spring.easy-log.enabled", "spring.easy-log.trace.enabled"}, havingValue = "true", matchIfMissing = true)
public class EasyLogTraceAutoConfiguration {


    /**按优先级选择链路追踪实现 zipkin>local*/
    @Configuration
    @ConditionalOnMissingBean(LogTracer.class)
    @Import({LogTraceConfiguration.ZipKinTranceConfig.class, LogTraceConfiguration.DefaultTraceConfig.class})
    static class ChooseLogTraceConfiguration {

    }
}
