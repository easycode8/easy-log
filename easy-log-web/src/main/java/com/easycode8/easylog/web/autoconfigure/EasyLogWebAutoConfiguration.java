package com.easycode8.easylog.web.autoconfigure;

import com.easycode8.easylog.core.aop.interceptor.LogAttributeSource;
import com.easycode8.easylog.web.controller.EasyLogController;
import com.easycode8.easylog.web.filter.SecurityBasicAuthFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "spring.easy-log.web.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(EasyLogWebProperties.class)
public class EasyLogWebAutoConfiguration {

    @Bean
    @ConditionalOnBean(LogAttributeSource.class)
    public EasyLogController easyLogController() {
        return new EasyLogController();
    }

    @Bean
    public SecurityBasicAuthFilter securityBasicAuthFilter(EasyLogWebProperties easyLogWebProperties) {
        return new SecurityBasicAuthFilter(easyLogWebProperties.getEnableBasicAuth(), easyLogWebProperties.getUsername(), easyLogWebProperties.getPassword());
    }
}
