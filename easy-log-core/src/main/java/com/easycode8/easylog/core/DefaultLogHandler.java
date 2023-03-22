package com.easycode8.easylog.core;

import com.alibaba.fastjson.JSON;
import com.easycode8.easylog.core.provider.OperatorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class DefaultLogHandler extends AbstractLogDataHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLogHandler.class);

    public DefaultLogHandler(OperatorProvider operatorProvider) {
        super(operatorProvider);
    }

    @Override
    public void before(LogInfo info, Method method, Class<?> targetClass) {
        LOGGER.info("[easy-log][{}]--begin content:{}", info.getTitle(), JSON.toJSONString(info));
    }

    @Override
    public void after(LogInfo info, Method method, Class<?> targetClass) {
        LOGGER.info("[easy-log][{}]--end timeout:{} exception:{}", info.getTitle(), info.getTimeout(), info.getException());

    }
}
