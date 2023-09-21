package com.easycode8.easylog.web.filter;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.annotation.PostConstruct;


public class LogFilter extends Filter<ILoggingEvent>  {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogFilter.class);

    private Encoder<ILoggingEvent> encoder;


    private final SimpMessagingTemplate messagingTemplate;

    public LogFilter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    @Override
    public FilterReply decide(ILoggingEvent event) {
        messagingTemplate.convertAndSend("/topic/logback", new String(encoder.encode(event)));
        return FilterReply.NEUTRAL;
    }

    @PostConstruct
    public void configureLogback() {
        // 获取LoggerContext
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        // 查找ConsoleAppender
        ConsoleAppender<?> consoleAppender = findConsoleAppender(context);
        if (consoleAppender != null) {
            // 创建并添加自定义过滤器
            Filter customFilter = this;
            consoleAppender.addFilter(customFilter);
            encoder = (Encoder<ILoggingEvent>) consoleAppender.getEncoder();
        }
    }


    private ConsoleAppender<?> findConsoleAppender(LoggerContext context) {
        // 在LoggerContext中查找ConsoleAppender
        // 这里假设ConsoleAppender的名称为"CONSOLE"或"console"
        ConsoleAppender<?> appender = (ConsoleAppender<?>) context.getLogger(Logger.ROOT_LOGGER_NAME).getAppender("CONSOLE");
        if (appender == null) {
            appender = (ConsoleAppender<?>) context.getLogger(Logger.ROOT_LOGGER_NAME).getAppender("console");
        }
        if (appender == null) {
            throw new IllegalStateException("websocket在线日志未发现logback console appender配置");
        }
        return appender;
    }
}