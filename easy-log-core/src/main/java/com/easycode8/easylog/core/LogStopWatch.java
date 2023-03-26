package com.easycode8.easylog.core;

import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.StopWatch;

public class LogStopWatch {
//    private static final ThreadLocal<LogStopWatch> STOP_WATCH = new NamedThreadLocal<>("STOP_WATCH");


    private Logger logger;
    private StopWatch stopWatch;



    public LogStopWatch(Logger logger, String stopWatchName) {
        this.logger = logger;
        this.stopWatch = new StopWatch(stopWatchName);
    }


    public LogStopWatch stop() {
        if (logger.isDebugEnabled()) {
            stopWatch.stop();
        }

        return this;
    }

    public void start(String message, String... variable) {
        if (logger.isDebugEnabled()) {
            stopWatch.start(MessageFormatter.arrayFormat(message, variable).getMessage());
        }

    }

    public void start(String taskName) {
        if (logger.isDebugEnabled()) {
            stopWatch.start(taskName);
        }

    }


    public void showDetail() {
//        for (StopWatch.TaskInfo taskInfo : stopWatch.getTaskInfo()) {
//            stopWatch.getLastTaskInfo().getTimeMillis();
//            System.out.println("任务:" + taskInfo.getTaskName() + " timeout:" + taskInfo.getTimeNanos());
//        }
        if (logger.isDebugEnabled()) {
            logger.debug(stopWatch.prettyPrint());
        }

    }


}
