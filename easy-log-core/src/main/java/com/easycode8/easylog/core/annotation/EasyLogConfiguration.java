package com.easycode8.easylog.core.annotation;


import com.easycode8.easylog.core.DefaultLogHandler;
import com.easycode8.easylog.core.LogDataHandler;
import com.easycode8.easylog.core.aop.BeanFactoryLogAttributeSourceAdvisor;
import com.easycode8.easylog.core.aop.interceptor.AnnotationLogAttributeSource;
import com.easycode8.easylog.core.aop.interceptor.LogAttributeSource;
import com.easycode8.easylog.core.aop.interceptor.LogMethodInterceptor;
import com.easycode8.easylog.core.provider.OperatorProvider;
import com.easycode8.easylog.core.provider.SessionOperatorProvider;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(EasyLogProperties.class)
public class EasyLogConfiguration {
    @Bean
    public Advisor logStaticMethodMatcherPointcutAdvisor(LogAttributeSource logAttributeSource, LogMethodInterceptor logMethodInterceptor) {
        //Advisor 是 Spring AOP 对 Advice 和 Pointcut 的抽象，可以理解为“执行通知者”，一个 Pointcut （一般对应方法）和用于“增强”它的 Advice 共同组成这个方法的一个 Advisor
        BeanFactoryLogAttributeSourceAdvisor advisor = new BeanFactoryLogAttributeSourceAdvisor();
        advisor.setLogAttributeSource(logAttributeSource);
        advisor.setAdvice(logMethodInterceptor);
        return advisor;
    }

    @Bean
    public LogMethodInterceptor logMethodInterceptor(LogAttributeSource logAttributeSource) {
        LogMethodInterceptor logMethodInterceptor = new LogMethodInterceptor();
        logMethodInterceptor.setLogAttributeSource(logAttributeSource);

        return logMethodInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public LogAttributeSource logAttributeSource(EasyLogProperties easyLogProperties) {
        return new AnnotationLogAttributeSource(easyLogProperties);
    }


    @Bean
    @ConditionalOnMissingBean
    public LogDataHandler logDataHandler(ObjectProvider<OperatorProvider> operatorProvider) {
        return new DefaultLogHandler(operatorProvider.getIfAvailable());
    }

    @Bean
    @ConditionalOnMissingBean
    public OperatorProvider operatorProvider(EasyLogProperties easyLogProperties) {
        return new SessionOperatorProvider(easyLogProperties);
    }


}
