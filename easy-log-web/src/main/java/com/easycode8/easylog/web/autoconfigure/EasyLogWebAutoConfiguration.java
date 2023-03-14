package com.easycode8.easylog.web.autoconfigure;

import com.easycode8.easylog.core.aop.interceptor.LogAttributeSource;
import com.easycode8.easylog.web.controller.EasyLogController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "spring.easy-log.web.enabled", havingValue = "true", matchIfMissing = true)
public class EasyLogWebAutoConfiguration {

    @Bean
    @ConditionalOnBean(LogAttributeSource.class)
    public EasyLogController easyLogController() {
        return new EasyLogController();
    }
}
