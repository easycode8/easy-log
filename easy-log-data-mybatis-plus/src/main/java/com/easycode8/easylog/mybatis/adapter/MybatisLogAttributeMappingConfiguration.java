package com.easycode8.easylog.mybatis.adapter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public abstract class MybatisLogAttributeMappingConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisLogAttributeMappingConfiguration.class);

    @ConditionalOnClass(BaseMapper.class)
    public static class MybatisPlus {
        @Bean
        @ConditionalOnMissingBean
        public MybatisLogAttributeMappingAdapter mybatisPlusLogDataHandler() {
            LOGGER.info("[easy-log]启动mybatis-plus mapper操作日志记录");
            return new MybatisPlusAdapter();
        }
    }

    public static class Mybatis {
        @Bean
        @ConditionalOnMissingBean
        public MybatisLogAttributeMappingAdapter mybatisLogDataHandler() {
            LOGGER.info("[easy-log]启动mybatis mapper操作日志记录");
            return new MybatisAdapter();
        }
    }
}
