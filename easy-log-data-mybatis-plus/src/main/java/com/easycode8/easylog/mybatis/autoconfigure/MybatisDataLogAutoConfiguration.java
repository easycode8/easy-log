package com.easycode8.easylog.mybatis.autoconfigure;

import com.easycode8.easylog.mybatis.adapter.MybatisLogAttributeMappingAdapter;
import com.easycode8.easylog.mybatis.adapter.MybatisLogAttributeMappingConfiguration;
import com.easycode8.easylog.mybatis.handler.DataSnapshotHandler;
import com.easycode8.easylog.mybatis.handler.MybatisPlusDataSnapshotHandler;
import com.easycode8.easylog.mybatis.interceptor.DataSnapshotInterceptor;
import org.apache.ibatis.mapping.MappedStatement;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;



@ConditionalOnClass(MappedStatement.class)
@Configuration
@ConditionalOnProperty(name = "spring.easy-log.scan-mybatis-plus.enabled", havingValue = "true")
@EnableConfigurationProperties(EasyLogMybatisPlusProperties.class)
public class MybatisDataLogAutoConfiguration {

    @Bean
    public DataSnapshotInterceptor recordInterceptor(JdbcTemplate jdbcTemplate, DataSnapshotHandler dataSnapshotHandler) {
        return new DataSnapshotInterceptor(jdbcTemplate, dataSnapshotHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSnapshotHandler mybatisPlusDataSnapshotHandler() {
        return new MybatisPlusDataSnapshotHandler();
    }

    /**
     * mybtatis日志属性选择器支持mybatis及mybatis-plus 优先级:mybatis-plus > mybatis
     */
    @Configuration
    @ConditionalOnMissingBean(MybatisLogAttributeMappingAdapter.class)
    @Import({MybatisLogAttributeMappingConfiguration.MybatisPlus.class, MybatisLogAttributeMappingConfiguration.Mybatis.class})
    public static class ChooseMybatisLogAttributeMapping {

    }
}
