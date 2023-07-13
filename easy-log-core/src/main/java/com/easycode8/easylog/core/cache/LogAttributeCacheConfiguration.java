package com.easycode8.easylog.core.cache;

import com.easycode8.easylog.core.annotation.EasyLogProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

public abstract class LogAttributeCacheConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAttributeCacheConfiguration.class);

    @ConditionalOnProperty(value = "spring.easy-log.cache.redis.enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnClass(StringRedisTemplate.class)
    public static class RedisCacheConfiguration {
        @Bean
        @ConditionalOnBean(StringRedisTemplate.class)
        @ConditionalOnMissingBean(LogAttributeCache.class)
        public LogAttributeCache logAttributeCache (StringRedisTemplate stringRedisTemplate, EasyLogProperties easyLogProperties) {
            LOGGER.info("[easy-log]日志属性--使用redis缓存");
            return new LogAttributeRedisCache(stringRedisTemplate, easyLogProperties);
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
