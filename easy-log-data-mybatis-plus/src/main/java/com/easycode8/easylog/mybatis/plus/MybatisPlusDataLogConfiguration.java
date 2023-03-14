package com.easycode8.easylog.mybatis.plus;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycode8.easylog.mybatis.plus.interceptor.MybatisPlusMethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnClass(BaseMapper.class)
@Configuration
public class MybatisPlusDataLogConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisPlusDataLogConfiguration.class);

    @Bean
    public AspectJExpressionPointcutAdvisor mybatisPlusMethodAdvisor(MybatisPlusMethodInterceptor interceptor) {
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression("execution(* com.easycode8.easylog.sample.mapper.*.*(..))");
        advisor.setAdvice(interceptor);
        LOGGER.info("[easy-log]启动mybatis-plus操作数据比对");
        return advisor;
    }


    @Bean
    public MybatisPlusMethodInterceptor mybatisPlusMethodInterceptor() {
        return new MybatisPlusMethodInterceptor();
    }
}
