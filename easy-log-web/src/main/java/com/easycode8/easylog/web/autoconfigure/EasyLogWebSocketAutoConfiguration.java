package com.easycode8.easylog.web.autoconfigure;

import ch.qos.logback.classic.LoggerContext;
import com.easycode8.easylog.web.filter.LogFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@ConditionalOnProperty(value = "spring.easy-log.web.websocket.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(StompEndpointRegistry.class)
@EnableWebSocketMessageBroker
public class EasyLogWebSocketAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyLogWebSocketAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public WebSocketMessageBrokerConfigurer brokerConfigurer() {

        return new WebSocketMessageBrokerConfigurer() {
            /**
             * stomp 协议，一种格式比较简单且被广泛支持的通信协议，spring4提供了以stomp协议为基础的websocket通信实现。
             * spring 的websocket实现，实际上是一个简易版的消息队列（而且是主题-订阅模式的）
             * @param registry
             */
            @Override
            public void registerStompEndpoints(StompEndpointRegistry registry) {
                // "/websocket"，客户端需要注册这个端点进行链接，
                // .withSockJS()的作用是声明我们想要使用 SockJS 功能，如果WebSocket不可用的话，会使用 SockJS。
                registry.addEndpoint("/websocket-stomp")
                        .setAllowedOrigins("*")
                        .withSockJS();


            }
        };
    }

    @Bean
    @ConditionalOnClass(LoggerContext.class)
    public LogFilter easyLogOnlineLogFilter(SimpMessagingTemplate simpMessagingTemplate) {
        LOGGER.info("[easy-log]启动web在线实时日志(websocket)");
        return new LogFilter(simpMessagingTemplate);
    }

}
