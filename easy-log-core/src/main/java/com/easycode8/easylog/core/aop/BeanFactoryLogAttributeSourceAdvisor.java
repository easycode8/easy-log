package com.easycode8.easylog.core.aop;

import com.easycode8.easylog.core.aop.interceptor.LogAttributeSource;
import com.easycode8.easylog.core.aop.interceptor.LogAttributeSourcePointcut;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.lang.Nullable;

public class BeanFactoryLogAttributeSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor {
    @Nullable
    private LogAttributeSource logAttributeSource;

    private final LogAttributeSourcePointcut logAttributeSourcePointcut = new LogAttributeSourcePointcut() {
        @Override
        protected LogAttributeSource getLogAttributeSource() {
            return logAttributeSource;
        }
    };

    @Override
    public Pointcut getPointcut() {
        return this.logAttributeSourcePointcut;
    }

    public void setLogAttributeSource(LogAttributeSource logAttributeSource) {
        this.logAttributeSource = logAttributeSource;
    }
}
