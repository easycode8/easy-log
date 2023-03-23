package com.easycode8.easylog.core.aop.interceptor;


import com.easycode8.easylog.core.LogDataHandler;
import com.easycode8.easylog.core.LogInfo;
import com.easycode8.easylog.core.provider.OperatorProvider;
import com.easycode8.easylog.core.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * BeanFactoryAware 作用是通过日志注解属性中定义bean名称去spring容器中寻找对应的处理器
 * InitializingBean 校验必须需要的资源
 */
public abstract class LogAspectSupport implements BeanFactoryAware , InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspectSupport.class);

    private static final ThreadLocal<LogInfo> logInfoHolder = new NamedThreadLocal<>("current thread log info");

    private LogAttributeSource logAttributeSource;

    private LogDataHandler logDataHandler;

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private OperatorProvider operatorProvider;

    @Nullable
    private BeanFactory beanFactory;

    protected Object invoke(Method method, @Nullable Class<?> targetClass,Object[] args,
                                             final InvocationCallback invocation) throws Throwable {
        // 获取日志属性（是否强事务,日志保存成功才算业务成功）
        LogAttribute logAttribute = getLogAttributeSource().getLogAttribute(method, targetClass);
        final LogDataHandler handle = this.determineLogHandleAdapter(logAttribute);
        boolean isAsync = logAttribute.async();

        // 创建日志信息
        long startTime = System.currentTimeMillis();
        LogInfo info = handle.init(logAttribute, method, args, targetClass);
        LogUtils.initLog(info, logAttribute, method, args, targetClass);
        // 如果操作人为空,尝试从上下文获取
        if (operatorProvider != null && StringUtils.isEmpty(info.getOperator())) {
            info.setOperator(operatorProvider.currentOperator());
        }
        logInfoHolder.set(info);
        info.setStatus(LogInfo.STATUS_INIT);
        Object retVal;
        try {
            // This is an around advice: Invoke the next interceptor in the chain.
            // This will normally result in a target object being invoked.
            handle.before(info, method, args, targetClass);
            info.setStatus(LogInfo.STATUS_BEFORE);
            retVal = invocation.proceedWithLog();
            info.setTimeout(System.currentTimeMillis() - startTime);
            info.setStatus(LogInfo.STATUS_FINISH);
            // 如果是登录接口可能登录后才有用户信息,所以这个补充设置一次
            if (operatorProvider != null && StringUtils.isEmpty(info.getOperator())) {
                info.setOperator(operatorProvider.currentOperator());
            }
            if (isAsync) {
                threadPoolTaskExecutor.execute(() -> handle.after(info, method, targetClass));
            } else {
                handle.after(info, method, targetClass);
            }


        } catch (Throwable ex) {

            info.setTimeout(System.currentTimeMillis() - startTime);
            if (LogInfo.STATUS_INIT == info.getStatus()) {
                LOGGER.error("[easy-log] handle before {} error:{}", LogUtils.createDefaultTitle(method, targetClass), ex.getMessage(), ex);
            } else if (LogInfo.STATUS_BEFORE == info.getStatus()) { // 业务执行不成功记录失败原因
                info.setException(ex.getMessage());
                if (isAsync) {
                    threadPoolTaskExecutor.execute(() -> handle.after(info, method, targetClass));
                } else {
                    handle.after(info, method, targetClass);
                }

            } else if (LogInfo.STATUS_FINISH == info.getStatus()) { // 业务执行成功,但是日志后处理失败,提示错误日志。这时候业务会因为异常回滚操作
                LOGGER.error("[easy-log] handle after {} error:{}", LogUtils.createDefaultTitle(method, targetClass), ex.getMessage(), ex);
            }

            throw ex;
        } finally {
            logInfoHolder.remove();
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
            return getLogDataHandler();
        }
        String qualifier = logAttribute.handler();
        if (StringUtils.hasText(qualifier)) {
            // TODO 补充从缓存中获取 see org.springframework.transaction.interceptor.TransactionAspectSupport.determineQualifiedTransactionManager
            LogDataHandler logDataHandler = BeanFactoryAnnotationUtils.qualifiedBeanOfType(
                    beanFactory, LogDataHandler.class, qualifier);


            return logDataHandler;

        } else {
            LogDataHandler defaultLogDataHandler = getLogDataHandler();
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
        if (getLogDataHandler() == null && this.beanFactory == null) {
            throw new IllegalStateException(
                    "Set the 'LogDataHandler' property or make sure to run within a BeanFactory " +
                            "containing a LogDataHandler bean!");
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

    public LogDataHandler getLogDataHandler() {
        return logDataHandler;
    }

    public void setLogDataHandler(LogDataHandler logDataHandler) {
        this.logDataHandler = logDataHandler;
    }

    public void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    public void setOperatorProvider(OperatorProvider operatorProvider) {
        this.operatorProvider = operatorProvider;
    }
}
