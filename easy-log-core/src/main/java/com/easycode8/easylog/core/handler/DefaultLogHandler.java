package com.easycode8.easylog.core.handler;

import com.easycode8.easylog.core.LogInfo;
import com.easycode8.easylog.core.aop.interceptor.LogAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class DefaultLogHandler implements LogDataHandler<LogInfo> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLogHandler.class);


    @Override
    public LogInfo init(LogAttribute logAttribute, Method method, Object[] args, Class<?> targetClass) {
        return new LogInfo();
    }

    @Override
    public void before(LogInfo info, Method method, Object[] args, Class<?> targetClass, Object targetObject) {
        LOGGER.info("[easy-log][{}]--begin operator:[{}] param:{}", info.getTitle(), info.getOperator(), info.getParams());
    }

    @Override
    public void after(LogInfo info, Method method, Class<?> targetClass, Object returnValue) {
        if (LogInfo.STATUS_FINISH == info.getStatus()) {
            if (LogInfo.TYPE_DAO.equals(info.getType())) {
                LOGGER.info("[easy-log][{}]--end timeout:{} dataSnapshot:{}", info.getTitle(), info.getTimeout(), info.getDataSnapshot());
            } else {
                LOGGER.info("[easy-log][{}]--end timeout:{} ", info.getTitle(), info.getTimeout());
            }        } else {
            LOGGER.warn("[easy-log][{}]--end timeout:{} exception:{}", info.getTitle(), info.getTimeout(), info.getException());
        }
    }
}
