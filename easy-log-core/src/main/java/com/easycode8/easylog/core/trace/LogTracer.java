package com.easycode8.easylog.core.trace;


import com.easycode8.easylog.core.LogInfo;

public interface LogTracer {
    void init(LogInfo logInfo);
    void start(LogInfo logInfo);
    void finish(LogInfo logInfo);

}
