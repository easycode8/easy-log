package com.easycode8.easylog.core;

import com.alibaba.fastjson.JSON;
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
        LOGGER.info("[easy-log][{}]--begin content:{}", info.getTitle(), JSON.toJSONString(info));
    }

    @Override
    public void after(LogInfo info, Method method, Class<?> targetClass) {
        if (LogInfo.STATUS_FINISH == info.getStatus()) {
            LOGGER.info("[easy-log][{}]--end timeout:{}", info.getTitle(), info.getTimeout());
        } else {
            LOGGER.warn("[easy-log][{}]--end timeout:{} exception:{}", info.getTitle(), info.getTimeout(), info.getException());
        }
    }
}
