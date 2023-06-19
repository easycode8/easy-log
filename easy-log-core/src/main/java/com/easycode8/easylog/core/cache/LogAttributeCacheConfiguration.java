package com.easycode8.easylog.core.cache;

import com.easycode8.easylog.core.annotation.EasyLogProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

public abstract class LogAttributeCacheConfiguration {

    @ConditionalOnProperty(value = "spring.easy-log.cache.redis.enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnClass(StringRedisTemplate.class)
    public static class RedisCacheConfiguration {
        @Bean
        @ConditionalOnBean(StringRedisTemplate.class)
        @ConditionalOnMissingBean(LogAttributeCache.class)
        public LogAttributeCache logAttributeCache (StringRedisTemplate stringRedisTemplate, EasyLogProperties easyLogProperties) {
            return new LogAttributeRedisCache(stringRedisTemplate, easyLogProperties);
        }
    }

    public static class LocalCacheConfiguration {
        @Bean
        @ConditionalOnMissingBean(LogAttributeCache.class)
        public LogAttributeCache logAttributeCache () {
            return new LogAttributeMemoryCache();
        }
    }
}
