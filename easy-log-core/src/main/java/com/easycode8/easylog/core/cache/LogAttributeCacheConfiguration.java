package com.easycode8.easylog.core.cache;

import com.easycode8.easylog.core.annotation.EasyLogProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

public abstract class LogAttributeCacheConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAttributeCacheConfiguration.class);

    @ConditionalOnProperty(value = "spring.easy-log.cache.redis.enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnClass(RedisTemplate.class)
    public static class RedisCacheConfiguration {
        @Bean
        @ConditionalOnBean(RedisTemplate.class)
        @ConditionalOnMissingBean(LogAttributeCache.class)
        public LogAttributeCache logAttributeCache (@Qualifier("redisTemplate") RedisTemplate redisTemplate, EasyLogProperties easyLogProperties) {
            LOGGER.info("[easy-log]启动redis缓存日志属性(spring.easy-log.cache.redis.enabled 可控制关闭)");
            return new LogAttributeRedisCache(redisTemplate, easyLogProperties);
        }


        // 配置redis事件监听器
        @Bean
        public RedisMessageListenerContainer redisMessageListenerContainer(
                RedisConnectionFactory connectionFactory, LogAttributeRedisCache messageListener) {
            RedisMessageListenerContainer container = new RedisMessageListenerContainer();
            container.setConnectionFactory(connectionFactory);
            // 配置监听的频道或模式
            container.addMessageListener(messageListener, new PatternTopic("easy-log.*"));
            return container;
        }
    }

    public static class LocalCacheConfiguration {
        @Bean
        @ConditionalOnMissingBean(LogAttributeCache.class)
        public LogAttributeCache logAttributeCache () {
            LOGGER.info("[easy-log]日志属性--使用本地内存缓存");
            return new LogAttributeMemoryCache();
        }
    }
}
