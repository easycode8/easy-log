package com.easycode8.easylog.autoconfigure;


import com.easycode8.easylog.core.adapter.ServiceLogAttributeMapping;
import com.easycode8.easylog.core.annotation.EasyLogProperties;
import com.easycode8.easylog.core.annotation.EnableEasyLog;
import com.easycode8.easylog.core.aop.interceptor.LogAttributeSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableEasyLog
@ConditionalOnProperty(value = "spring.easy-log.enabled", havingValue = "true", matchIfMissing = true)
public class EasyLogAutoConfiguration {


    /**
     * 多个属性源使用按优先级加载,覆盖core包中默认逻辑。因为swagger包
     * 在日志框架中不是必须得,因此使用swagger注解得属性源,不在core包中实现
     *
     * */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(LogAttributeSource.class)
    @Import({LogAttributeSourceConfiguration.SwaggerSource.class, LogAttributeSourceConfiguration.EasyLogSource.class})
    protected static class ChooseLogAttributeSourceConfiguration {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnProperty(value = "spring.easy-log.scan-service.enabled", havingValue = "true")
        public ServiceLogAttributeMapping serviceLogAttributeMapping(EasyLogProperties easyLogProperties) {
            return new ServiceLogAttributeMapping(easyLogProperties);
        }
    }



}
