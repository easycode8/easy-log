package com.easycode8.easylog.core.aop.interceptor;


import com.easycode8.easylog.core.LogDataHandler;
import com.easycode8.easylog.core.LogInfo;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * BeanFactoryAware 作用是通过日志注解属性中定义bean名称去spring容器中寻找对应的处理器
 * InitializingBean 校验必须需要的资源
 */
public abstract class LogAspectSupport implements BeanFactoryAware , InitializingBean {

    private static final ThreadLocal<LogInfo> logInfoHolder = new NamedThreadLocal<>("current thread log info");

    private LogAttributeSource logAttributeSource;

    private LogDataHandler logDataHandler;


    @Nullable
    private BeanFactory beanFactory;

    protected Object invoke(Method method, @Nullable Class<?> targetClass,Object[] args,
                                             final InvocationCallback invocation) throws Throwable {
        // 获取日志属性（是否强事务,日志保存成功才算业务成功）
        LogAttribute logAttribute = getLogAttributeSource().getLogAttribute(method, targetClass);
        final LogDataHandler handle = this.determineLogHandleAdapter(logAttribute);

        // 创建日志信息
        long startTime = System.currentTimeMillis();
        LogInfo info = handle.init(logAttribute, method, args, targetClass);
        logInfoHolder.set(info);

        Object retVal;
        try {
            // This is an around advice: Invoke the next interceptor in the chain.
            // This will normally result in a target object being invoked.
            handle.before(info, method, targetClass);
            retVal = invocation.proceedWithLog();

        }
        catch (Throwable ex) {
            // target invocation exception
            //completeTransactionAfterThrowing(txInfo, ex);
            info.setException(ex.getMessage());
            throw ex;
        }
        finally {
            info.setTimeout(System.currentTimeMillis() - startTime);
            //cleanupTransactionInfo(txInfo);
            logInfoHolder.remove();

        }

        try {
            handle.after(info, method, targetClass);
        } catch (Exception e) {
            // 日志处理失败忽略处理
        }

        return retVal;
    }

    /**
     * 支持注解中指定 自定义日志处理器
     * @param logAttribute
     * @return
     */
    protected LogDataHandler determineLogHandleAdapter(LogAttribute logAttribute) {
        if (logAttribute == null || this.beanFactory == null) {
            return getLogHandleAdapter();
        }
        String qualifier = logAttribute.handler();
        if (StringUtils.hasText(qualifier)) {
            // TODO 补充从缓存中获取 see org.springframework.transaction.interceptor.TransactionAspectSupport.determineQualifiedTransactionManager
            LogDataHandler logDataHandler = BeanFactoryAnnotationUtils.qualifiedBeanOfType(
                    beanFactory, LogDataHandler.class, qualifier);


            return logDataHandler;

        } else {
            LogDataHandler defaultLogDataHandler = getLogHandleAdapter();
            if (defaultLogDataHandler == null) {
                defaultLogDataHandler = this.beanFactory.getBean(LogDataHandler.class);
            }
            return defaultLogDataHandler;

        }

    }


    /**
     * Simple callback interface for proceeding with the target invocation.
     * Concrete interceptors/aspects adapt this to their invocation mechanism.
     */
    @FunctionalInterface
    protected interface InvocationCallback {

        @Nullable
        Object proceedWithLog() throws Throwable;
    }

    @Override
    public void afterPropertiesSet() {
        if (getLogHandleAdapter() == null && this.beanFactory == null) {
            throw new IllegalStateException(
                    "Set the 'logHandleAdapter' property or make sure to run within a BeanFactory " +
                            "containing a logHandleAdapter bean!");
        }
        if (getLogAttributeSource() == null) {
            throw new IllegalStateException(
                    "Either 'LogAttributeSource' or 'LogAttribute' is required: " +
                            "If there are no log methods, then don't use a log aspect.");
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public LogAttributeSource getLogAttributeSource() {
        return logAttributeSource;
    }

    public void setLogAttributeSource(LogAttributeSource logAttributeSource) {
        this.logAttributeSource = logAttributeSource;
    }

    public LogDataHandler getLogHandleAdapter() {
        return logDataHandler;
    }

    public void setLogHandleAdapter(LogDataHandler logDataHandler) {
        this.logDataHandler = logDataHandler;
    }
}
