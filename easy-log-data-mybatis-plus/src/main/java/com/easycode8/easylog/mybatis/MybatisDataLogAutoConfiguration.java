package com.easycode8.easylog.mybatis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
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
}
