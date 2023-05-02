package com.easycode8.easylog.core.annotation;


import com.easycode8.easylog.core.DefaultLogHandler;
import com.easycode8.easylog.core.adapter.LogAttributeMappingAdapter;
import com.easycode8.easylog.core.LogDataHandler;
import com.easycode8.easylog.core.aop.BeanFactoryLogAttributeSourceAdvisor;
import com.easycode8.easylog.core.aop.interceptor.AnnotationLogAttributeSource;
import com.easycode8.easylog.core.aop.interceptor.LogAttributeSource;
import com.easycode8.easylog.core.aop.interceptor.LogMethodInterceptor;
import com.easycode8.easylog.core.provider.OperatorProvider;
import com.easycode8.easylog.core.provider.SessionOperatorProvider;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableConfigurationProperties(EasyLogProperties.class)
public class EasyLogConfiguration {
    @Bean
    public Advisor easyLogStaticMethodMatcherPointcutAdvisor(LogAttributeSource logAttributeSource, LogMethodInterceptor logMethodInterceptor, EasyLogProperties easyLogProperties) {
        //Advisor 是 Spring AOP 对 Advice 和 Pointcut 的抽象，可以理解为“执行通知者”，一个 Pointcut （一般对应方法）和用于“增强”它的 Advice 共同组成这个方法的一个 Advisor
        BeanFactoryLogAttributeSourceAdvisor advisor = new BeanFactoryLogAttributeSourceAdvisor();
        advisor.setLogAttributeSource(logAttributeSource);
        advisor.setAdvice(logMethodInterceptor);
        //设置自定义优先级值越小优先级越高
        if (easyLogProperties.getAspectOrder() != null) {
            advisor.setOrder(easyLogProperties.getAspectOrder());
        }
        return advisor;
    }

    @Bean
    public LogMethodInterceptor easyLogMethodInterceptor(LogAttributeSource logAttributeSource,
                                                     @Qualifier("easyLogThreadPoolTaskExecutor") ThreadPoolTaskExecutor taskExecutor,
                                                     @Qualifier("easyLogDataHandler") LogDataHandler logDataHandler,
                                                     ObjectProvider<OperatorProvider> operatorProvider) {
        LogMethodInterceptor logMethodInterceptor = new LogMethodInterceptor();
        logMethodInterceptor.setLogAttributeSource(logAttributeSource);
        logMethodInterceptor.setThreadPoolTaskExecutor(taskExecutor);
        logMethodInterceptor.setLogDataHandler(logDataHandler);
        logMethodInterceptor.setOperatorProvider(operatorProvider.getIfAvailable());
        return logMethodInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public LogAttributeSource logAttributeSource(EasyLogProperties easyLogProperties, List<LogAttributeMappingAdapter> mappingAdapters) {
        return new AnnotationLogAttributeSource(easyLogProperties, mappingAdapters);
    }


    @Bean
    @ConditionalOnMissingBean(name = "easyLogDataHandler")
    public LogDataHandler easyLogDataHandler() {
        return new DefaultLogHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public OperatorProvider easyLogOperatorProvider(EasyLogProperties easyLogProperties) {
        return new SessionOperatorProvider(easyLogProperties);
    }

    /**
     * 日志处理异步执行线程池
     * @param easyLogProperties
     * @param objectProvider
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "easyLogThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor easyLogThreadPoolTaskExecutor(EasyLogProperties easyLogProperties, ObjectProvider<TaskDecorator> objectProvider) {
        EasyLogProperties.Task async = easyLogProperties.getTask();
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // 核心线程数定义了最小可以同时运行的线程数量
        // 1. cpu密集型CorePoolSize = CPU核心数+1
        // 2. IO密集型CorePoolSize = CPU核心数 * 2
        taskExecutor.setCorePoolSize(async.getCorePoolSize());
        // 当队列中存放的任务达到队列容量的时候，当前可以同时运行的线程数量变为最大线程数
        taskExecutor.setMaxPoolSize(async.getMaxPoolSize());
        // 任务队列，被提交但尚未被执行的任务
        taskExecutor.setQueueCapacity(async.getQueueCapacity());
        // 当线程池中的线程数量大于 corePoolSize 的时候，如果这时没有新的任务提交，核心线程外的线程不会立即销毁，而是会等待，直到等待的时间超过了 keepAliveTime才会被回收销毁
        taskExecutor.setKeepAliveSeconds(async.getKeepAliveSeconds());
        taskExecutor.setThreadNamePrefix(async.getThreadNamePrefix());
        //拒绝策略，当队列满了并且工作线程数量大于线程池的最大线程数时，提供拒绝策略
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setTaskDecorator(objectProvider.getIfAvailable());
        taskExecutor.initialize();
        return taskExecutor;
    }




}
