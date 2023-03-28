package com.easycode8.easylog.mybatis.plus.autoconfigure;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycode8.easylog.mybatis.plus.MybatisPlusLogDataHandler;
import com.easycode8.easylog.mybatis.plus.util.MybatisPlusLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnClass(BaseMapper.class)
@ConditionalOnProperty(name = "spring.easy-log.scan-mybatis-plus.enabled", havingValue = "true")
@Configuration
@EnableConfigurationProperties(EasyLogMybatisPlusProperties.class)
public class MybatisPlusDataLogConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisPlusDataLogConfiguration.class);


    @Bean(name = MybatisPlusLogUtils.LOG_DATA_HANDLER)
    @ConditionalOnMissingBean(name = MybatisPlusLogUtils.LOG_DATA_HANDLER)
    public MybatisPlusLogDataHandler mybatisPlusLogDataHandler() {
        LOGGER.info("[easy-log]启动mybatis-plus mapper操作日志记录");
        return new MybatisPlusLogDataHandler();
    }
}
