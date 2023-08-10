package com.easycode8.easylog.trace;


import com.easycode8.easylog.core.LogInfo;
import com.easycode8.easylog.core.trace.LogTracer;
import com.easycode8.easylog.trace.filter.MDCConstants;
import org.slf4j.MDC;

/**
 * 默认的日志追踪--暂不实现
 */
public class DefaultLogTracer implements LogTracer {

    @Override
    public void init(LogInfo logInfo) {
        logInfo.setTraceId(MDC.get(MDCConstants.TRACE_ID));
    }

    @Override
    public void start(LogInfo logInfo) {

    }

    @Override
    public void finish(LogInfo logInfo) {

    }

}
